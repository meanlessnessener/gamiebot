package GamieBot.infra.terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import GamieBot.adapter.controller.terminal.ITerminalController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimal TerminalBot implementation backed by a Unix domain socket server.
 * - run() starts a UNIX domain socket server at sockets/terminal.sock
 * - opens a 'foot' terminal running the Java client (TerminalClient) to act as a server command console
 * - supports registering user clients which can send messages to the server
 * - sendMessage(userName, message) sends a message to a connected user
 *
 * Notes:
 * - Client processes should connect using the included TerminalClient class (or any AF_UNIX client)
 * - Message protocol (simple line-based):
 *   REGISTER <userName>  -- first message from client to identify itself
 *   MSG <text>           -- messages from client to server
 *   FROM_SERVER <text>   -- messages sent from server to client
 */
public class TerminalBot {
    private static final Logger log = LoggerFactory.getLogger(TerminalBot.class);
    private static final Path SOCKET_DIR = Paths.get("sockets");
    private static final Path SOCKET_PATH = SOCKET_DIR.resolve("terminal.sock");

    private ITerminalController controller;
    private final Map<String, SocketChannel> users = Collections.synchronizedMap(new HashMap<>());
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public TerminalBot() {
    }

    public void setController(ITerminalController controller) {
        this.controller = controller;
    }

    /**
     * Start the Unix domain socket server and open a foot terminal running the TerminalClient
     * which can be used as a server-side command console.
     */
    public void run() {
        pool.submit(() -> {
            try {
                Files.createDirectories(SOCKET_DIR);
                // delete stale socket if present
                try {
                    Files.deleteIfExists(SOCKET_PATH);
                } catch (IOException e) {
                    log.warn("Could not delete existing socket: {}", e.getMessage());
                }

                ServerSocketChannel server = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
                UnixDomainSocketAddress address = UnixDomainSocketAddress.of(SOCKET_PATH);
                server.bind(address);

                log.info("UNIX domain socket server listening on {}", SOCKET_PATH);

                // Launch a server console in a new foot terminal. This runs the TerminalClient in "server-console" mode
                // so the operator can type commands like: addUser <name>, listUsers, help
                try {
                    String javaCmd = System.getProperty("java.home") + "/bin/java";
                    String classpath = System.getProperty("java.class.path");
                    ProcessBuilder pb = new ProcessBuilder("foot", "-e",
                            javaCmd, "-cp", classpath, "GamieBot.infra.terminal.TerminalClient",
                            "__server_console__", SOCKET_PATH.toString());
                    pb.inheritIO();
                    pb.start();
                } catch (Exception e) {
                    log.warn("Could not open foot terminal for server console: {}", e.getMessage());
                    log.info("You can run TerminalClient manually: java -cp <classpath> GamieBot.infra.terminal.TerminalClient __server_console__ {}", SOCKET_PATH.toString());
                }

                while (true) {
                    SocketChannel ch = server.accept();
                    log.info("Accepted connection: {}", ch.getRemoteAddress());
                    pool.submit(() -> handleClient(ch));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleClient(SocketChannel ch) {
        try (SocketChannel client = ch) {
            InputStream in = Channels.newInputStream(client);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            OutputStream out = Channels.newOutputStream(client);
            PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8);

            // expect a REGISTER <userName> as first non-empty line
            String line;
            String registeredUser = null;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (registeredUser == null) {
                    if (line.startsWith("REGISTER ")) {
                        registeredUser = line.substring("REGISTER ".length()).trim();
                        users.put(registeredUser, client);
                        log.info("User registered: {}", registeredUser);
                    } else if ("__server_console__".equals(line) || line.startsWith("CMD ")) {
                        // server console messages (ignored here)
                        log.info("Server console connected");
                    } else {
                        writer.println("FROM_SERVER ERROR: expected REGISTER <userName>");
                    }
                } else {
                    if (line.startsWith("MSG ")) {
                        String msg = line.substring(4);
                        log.info("Message from {}: {}", registeredUser, msg);
                        if (controller != null) {
                            controller.onMessageReceived(registeredUser, msg);
                        }
                    } else if (line.equals("PING")) {
                        writer.println("FROM_SERVER PONG");
                    } else {
                        // ignore unknown commands from client
                        log.debug("Unknown client line: {}", line);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Client handler error: {}", e.getMessage());
        } finally {
            // cleanup user mapping if present
            users.entrySet().removeIf(entry -> {
                try {
                    SocketChannel s = entry.getValue();
                    return !s.isOpen();
                } catch (Exception ex) {
                    return true;
                }
            });
        }
    }

    /**
     * Send a message to a connected user. Returns true if delivered (user exists and channel writable).
     */
    public boolean sendMessage(String userName, String message) {
        SocketChannel ch = users.get(userName);
        if (ch == null || !ch.isOpen()) return false;
        try {
            OutputStream out = Channels.newOutputStream(ch);
            PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8);
            writer.println(message);
            return true;
        } catch (Exception e) {
            log.error("Failed to send to {}: {}", userName, e.getMessage());
            return false;
        }
    }
}


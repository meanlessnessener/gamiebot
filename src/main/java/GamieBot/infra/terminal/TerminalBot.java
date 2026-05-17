package GamieBot.infra.terminal;

import java.io.*;
import java.net.*;
import java.net.StandardProtocolFamily;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import GamieBot.adapter.controller.terminal.ITerminalController;

public class TerminalBot {
    private static final Logger logger = LoggerFactory.getLogger(TerminalBot.class);
    private static final Path SOCKET_DIR = Paths.get("infra/terminal/sockets");
    private static final Path SOCKET_PATH = SOCKET_DIR.resolve("gamiebot.sock");

    private final Map<String, SocketClient> clients = Collections.synchronizedMap(new HashMap<>());
    private final AtomicReference<ITerminalController> listener = new AtomicReference<>();

    private ServerSocketChannel serverChannel;
    private Thread acceptorThread;

    public void setController(ITerminalController l) {
        logger.info("Setting controller: {}", l);
        listener.set(l);
    }

    public void onMessageReceived(String user, String message) {
        ITerminalController l = listener.get();
        if (l != null) {
            try { l.onMessageReceived(user, message); } catch (Throwable t) { System.out.println("(listener) " + t.getMessage()); }
        }
    }

    public void startUnixSocketServer() throws IOException {
        logger.info("Starting Unix socket server");
        
        if (!Files.exists(SOCKET_DIR)) Files.createDirectories(SOCKET_DIR);
        try { Files.deleteIfExists(SOCKET_PATH); } catch (IOException ignored) {}

        serverChannel = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
        UnixDomainSocketAddress addr = UnixDomainSocketAddress.of(SOCKET_PATH);
        serverChannel.bind(addr);

        acceptorThread = new Thread(() -> {
            while (serverChannel.isOpen()) {
                try {
                    SocketChannel ch = serverChannel.accept();
                    handleClientChannel(ch);
                } catch (IOException e) {
                    System.out.println("Accept error: " + e.getMessage());
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                }
            }
        }, "unix-acceptor");
        acceptorThread.setDaemon(true);
        acceptorThread.start();
        System.out.println("Unix socket server listening: " + SOCKET_PATH.toAbsolutePath());

        // Try to open a foot terminal to act as server console (client name: 'console')
        String classpath = System.getProperty("java.class.path");
        String javaCmd = "java -cp \"" + classpath + "\" GamieBot.infra.terminal.TerminalClient console";
        try {
            new ProcessBuilder("foot", "-e", "bash", "-ic", javaCmd + "; exec bash").start();
            System.out.println("Opened foot terminal for server console");
        } catch (IOException ex) {
            System.out.println("(notice) failed to open foot terminal: " + ex.getMessage());
            // fallback: start a headless JVM client
            try {
                new ProcessBuilder("java", "-cp", classpath, "GamieBot.infra.terminal.TerminalClient", "console").start();
                System.out.println("Started headless console client");
            } catch (IOException ex2) {
                System.out.println("Failed to start console client: " + ex2.getMessage());
            }
        }
    }

    private void handleClientChannel(SocketChannel ch) {
        logger.info("Handling client channel: {}", ch);
        
        Thread t = new Thread(() -> {
            String name = null;
            try (SocketChannel channel = ch;
                 BufferedReader r = new BufferedReader(new InputStreamReader(Channels.newInputStream(channel), StandardCharsets.UTF_8));
                 BufferedWriter w = new BufferedWriter(new OutputStreamWriter(Channels.newOutputStream(channel), StandardCharsets.UTF_8))) {

                // First line: HELLO <name>
                String first = r.readLine();
                if (first == null) return;
                if (first.startsWith("HELLO ")) name = first.substring(6).trim(); else name = UUID.randomUUID().toString();

                SocketClient client = new SocketClient(name, channel, r, w);
                clients.put(name, client);
                System.out.println("Client connected: " + name);
                onMessageReceived(name, "<connected>");

                String ln;
                if ("console".equals(name)) {
                    // special server console: interpret commands
                    w.write("Connected to server console. Type 'help'.\n");
                    w.flush();
                    while ((ln = r.readLine()) != null) {
                        String[] parts = ln.trim().split("\\s+", 3);
                        if (parts.length == 0 || parts[0].isEmpty()) continue;
                        String cmd = parts[0];
                        if (cmd.equals("help")) {
                            w.write("Commands:\n  list\n  send <client> <msg>\n  stop\n  quit\n");
                            w.flush();
                        } else if (cmd.equals("list")) {
                            w.write("Clients: " + clients.keySet() + "\n");
                            w.flush();
                        } else if (cmd.equals("send") && parts.length >= 3) {
                            String target = parts[1];
                            String msg = parts[2];
                            boolean ok = sendMessageToClient(target, msg);
                            w.write(ok ? "sent\n" : "no such client\n");
                            w.flush();
                        } else if (cmd.equals("adduser") && parts.length >= 2) {
                            String newUser = parts[1];
                            String classpath = System.getProperty("java.class.path");
                            String javaCmd = "java -cp \"" + classpath + "\" GamieBot.infra.terminal.TerminalClient " + newUser;
                            try {
                                new ProcessBuilder("foot", "-e", "bash", "-ic", javaCmd + "; exec bash").start();
                                w.write("Opened foot terminal for user: " + newUser + "\n");
                                w.flush();
                            } catch (IOException ex) {
                                try {
                                    new ProcessBuilder("java", "-cp", classpath, "GamieBot.infra.terminal.TerminalClient", newUser).start();
                                    w.write("Started headless client for user: " + newUser + "\n");
                                    w.flush();
                                } catch (IOException ex2) {
                                    w.write("Failed to start client for " + newUser + ": " + ex2.getMessage() + "\n");
                                    w.flush();
                                }
                            }
                        } else if (cmd.equals("stop")) {
                            w.write("stopping server\n");
                            w.flush();
                            stop();
                            break;
                        } else if (cmd.equals("quit")) {
                            w.write("bye\n");
                            w.flush();
                            break;
                        } else {
                            w.write("unknown command\n");
                            w.flush();
                        }
                    }
                } else {
                    while ((ln = r.readLine()) != null) {
                        onMessageReceived(name, ln);
                    }
                }
            } catch (IOException e) {
                System.out.println("Client " + name + " error: " + e.getMessage());
            } finally {
                if (name != null) {
                    clients.remove(name);
                    onMessageReceived(name, "<disconnected>");
                }
            }
        }, "client-handler");
        t.setDaemon(true);
        t.start();
    }

    public boolean sendMessageToClient(String clientName, String message) {
        logger.info("Sending message to client: {} -> {}", clientName, message);
        
        SocketClient sc = clients.get(clientName);
        if (sc == null) {
            logger.warn("Client not found: {}", clientName);
            return false;
        }
        try {
            sc.writer.write(message + "\n");
            sc.writer.flush();
            return true;
        } catch (IOException e) {
            logger.error("Write failed to {}: {}", clientName, e.getMessage());
            return false;
        }
    }

    /**
     * Backwards-compatible method used by TerminalPresenter and older code.
     */
    public boolean sendMessage(String userName, String message) {
        return sendMessageToClient(userName, message);
    }

    public void stop() {
        try { if (serverChannel != null) serverChannel.close(); } catch (IOException ignored) {}
        clients.values().forEach(SocketClient::close);
    }

    private static class SocketClient {
        final String name;
        final SocketChannel channel;
        final BufferedReader reader;
        final BufferedWriter writer;
        SocketClient(String name, SocketChannel ch, BufferedReader r, BufferedWriter w) { this.name = name; this.channel = ch; this.reader = r; this.writer = w; }
        void close() { try { channel.close(); } catch (IOException ignored) {} }
    }

    public static void main(String[] args) throws Exception {
        TerminalBot bot = new TerminalBot();
        bot.startUnixSocketServer();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("TerminalBot socket server running. Commands: list, send <name> <msg>, stop, quit");
        String line;
        while ((line = in.readLine()) != null) {
            String[] parts = line.trim().split("\\s+", 3);
            if (parts.length == 0 || parts[0].isEmpty()) continue;
            String cmd = parts[0];
            if (cmd.equals("list")) {
                System.out.println("Connected clients: " + bot.clients.keySet());
            } else if (cmd.equals("send") && parts.length >= 3) {
                String name = parts[1];
                String msg = parts[2];
                boolean ok = bot.sendMessageToClient(name, msg);
                System.out.println(ok ? "sent" : "no such client");
            } else if (cmd.equals("stop")) {
                bot.stop();
                System.out.println("stopped server");
            } else if (cmd.equals("quit")) {
                bot.stop();
                break;
            } else {
                System.out.println("unknown command");
            }
        }
    }
}

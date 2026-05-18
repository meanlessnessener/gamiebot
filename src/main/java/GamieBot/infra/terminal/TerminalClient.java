package GamieBot.infra.terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Simple interactive terminal client that connects to the TerminalBot Unix domain socket.
 * Usage:
 *  java -cp <classpath> GamieBot.infra.terminal.TerminalClient <userName> <socketPath>
 * Special mode: __server_console__ <socketPath> - acts as the server console and accepts commands:
 *   addUser <userName>  -- prints instruction for starting a new client process
 *   listUsers           -- prints nothing (server may log users)
 *   help                -- prints help
 */
public class TerminalClient {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: TerminalClient <userName|__server_console__> <socketPath>");
            return;
        }
        String user = args[0];
        Path socket = Paths.get(args[1]);

        try (SocketChannel ch = SocketChannel.open(StandardProtocolFamily.UNIX)) {
            UnixDomainSocketAddress addr = UnixDomainSocketAddress.of(socket);
            ch.connect(addr);

            try (BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
                 PrintWriter out = new PrintWriter(Channels.newOutputStream(ch), true, StandardCharsets.UTF_8);
                 BufferedReader in = new BufferedReader(new InputStreamReader(Channels.newInputStream(ch), StandardCharsets.UTF_8))) {

                if ("__server_console__".equals(user)) {
                    System.out.println("Server console connected. Commands: addUser <name>, listUsers, help");
                    // server console loop: read commands from stdin and spawn new foot terminals running TerminalClient
                    String cmd;
                    while ((cmd = stdin.readLine()) != null) {
                        if (cmd.trim().isEmpty()) continue;
                        if (cmd.startsWith("addUser ")) {
                            String name = cmd.substring("addUser ".length()).trim();
                            String javaCmd = System.getProperty("java.home") + "/bin/java";
                            String classpath = System.getProperty("java.class.path");
                            try {
                                ProcessBuilder pb = new ProcessBuilder("foot", "-e",
                                        javaCmd, "-cp", classpath, "GamieBot.infra.terminal.TerminalClient",
                                        name, socket.toString());
                                pb.inheritIO();
                                pb.start();
                                System.out.println("Spawned client '" + name + "' in new foot terminal.");
                            } catch (Exception e) {
                                System.err.println("Failed to spawn client: " + e.getMessage());
                                System.out.println("Fallback: java -cp " + classpath + " GamieBot.infra.terminal.TerminalClient " + name + " " + socket.toString());
                            }
                        } else if (cmd.equals("listUsers")) {
                            System.out.println("listUsers command sent (server logs users)");
                        } else if (cmd.equals("help")) {
                            System.out.println("Commands: addUser <name>, listUsers, help");
                        } else {
                            System.out.println("Unknown command: " + cmd);
                        }
                    }
                } else {
                    // normal user client: register and then interactive message loop
                    out.println("REGISTER " + user);
                    // start thread to print messages from server
                    Thread reader = new Thread(() -> {
                        String line;
                        try {
                            while ((line = in.readLine()) != null) {
                                System.out.println(line);
                            }
                        } catch (IOException e) {
                            System.err.println("Connection closed: " + e.getMessage());
                        }
                    }, "socket-reader");
                    reader.setDaemon(true);
                    reader.start();

                    System.out.println("Connected as " + user + ". Type messages and press Enter. Type /quit to exit.");
                    String msg;
                    while ((msg = stdin.readLine()) != null) {
                        if (msg.equalsIgnoreCase("/quit") || msg.equalsIgnoreCase("quit")) break;
                        out.println("MSG " + msg);
                    }
                }
            }
        }
    }
}

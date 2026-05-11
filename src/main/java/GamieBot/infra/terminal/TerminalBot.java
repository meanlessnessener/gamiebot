package GamieBot.infra.terminal;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Set;

import GamieBot.adapter.controller.terminal.ITerminalController;

import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
public class TerminalBot {
    private static final Path FIFO_DIR = Paths.get("infra/terminal/fifos");
    private final Map<String, User> users = Collections.synchronizedMap(new HashMap<>());
    // API listener for external use
    private final java.util.concurrent.atomic.AtomicReference<ITerminalController> listener = new java.util.concurrent.atomic.AtomicReference<>();

    /**
     * Set a listener to receive incoming user messages.
     */
    public void setController(ITerminalController l) { listener.set(l); }

    /**
     * Notify the configured listener about an incoming message. Public API.
     */
    public void onMessageReceived(String user, String message) {
        ITerminalController l = listener.get();
        if (l != null) {
            try { l.onMessageReceived(user, message); } catch (Throwable t) {
                System.out.println("(listener error) " + t.getMessage());
            }
        }
    }

    /**
     * Send a message from the bot to the specified user. Returns true on success.
     */
    public boolean sendMessage(String userName, String message) {
        User u = users.get(userName);
        if (u == null) return false;
        String ts = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
        String formatted = String.format("[bot -> %s] (%s) %s", userName, ts, message);
        try {
            // Open the out FIFO for read+write to avoid blocking when no external reader is attached.
            try (java.nio.channels.SeekableByteChannel ch = Files.newByteChannel(u.out, java.util.EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.READ))) {
                java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap((formatted + "\n").getBytes());
                while (bb.hasRemaining()) {
                    ch.write(bb);
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("(notice) failed to write to " + u.out + ": " + e.getMessage());
            return false;
        }
    }

    public void run() {
        TerminalBot app = new TerminalBot();
        while (true) {
            try {
                app.ensureFifoDir();
                break;
            } catch (IOException | InterruptedException e) {
                System.out.println("Failed to create FIFO directory: " + e.getMessage());
                System.out.println("Retrying in 5 seconds...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}
            }
        }
        System.out.println("Chat emulator started. Type 'help' for commands.");
        System.out.print("> ");
        try {
             app.commandLoop();
        } catch (IOException e) {
            System.out.println("Error in command loop: " + e.getMessage());
        }
    }

    private void ensureFifoDir() throws IOException, InterruptedException {
        if (!Files.exists(FIFO_DIR)) {
            Files.createDirectories(FIFO_DIR);
        }
    }

    private void commandLoop() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while (true) {
            line = in.readLine();
            if (line == null) {
                // If no attached console (running under a launcher), keep process alive
                if (System.console() == null) {
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                    continue;
                } else {
                    // console present but readLine returned null (EOF) -> exit loop
                    break;
                }
            }
            String[] parts = line.trim().split("\\s+", 3);
            if (parts.length == 0 || parts[0].isEmpty()) { System.out.print("> "); continue; }
            String cmd = parts[0].toLowerCase();
            try {
                switch (cmd) {
                    case "help":
                        printHelp();
                        break;
                    case "adduser":
                        if (parts.length < 2) { System.out.println("Usage: adduser <name>"); break; }
                        addUser(parts[1]);
                        break;
                    case "simulate":
                        if (parts.length < 3) { System.out.println("Usage: simulate <name> <message>"); break; }
                        simulate(parts[1], parts[2]);
                        break;
                    case "list":
                        listUsers();
                        break;
                    case "exit":
                        shutdown();
                        return;
                    default:
                        System.out.println("Unknown command. Type 'help'.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.print("> ");
        }
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  adduser <name>        - create user FIFOs and start watching them");
        System.out.println("  simulate <name> <msg> - send message from a simulated user");
        System.out.println("  list                  - list users and FIFO paths");
        System.out.println("  exit                  - quit");
        System.out.println();
        System.out.println("External use (from another shell):");
        System.out.println("  echo 'hello' > infra/terminal/fifos/<name>_in");
        System.out.println("  cat infra/terminal/fifos/<name>_out");
    }

    private void addUser(String name) throws IOException, InterruptedException {
        if (users.containsKey(name)) {
            System.out.println("User already exists: " + name);
            return;
        }
        Path in = FIFO_DIR.resolve(name + "_in");
        Path out = FIFO_DIR.resolve(name + "_out");
        createFifoIfMissing(in);
        createFifoIfMissing(out);
        User u = new User(name, in, out);
        users.put(name, u);
        startUserWatcher(u);
        System.out.println("User added. FIFOs:");
        System.out.println("  in:  " + in.toString());
        System.out.println("  out: " + out.toString());

        openTerminalForUser(u);
    }

    private void listUsers() {
        if (users.isEmpty()) System.out.println("No users.");
        else users.values().forEach(u -> System.out.println(u.name + " -> in:" + u.in + " out:" + u.out));
    }

    private void shutdown() {
        System.out.println("Shutting down...");
        users.values().forEach(User::stop);
    }

    private void simulate(String name, String msg) throws IOException {
        User u = users.get(name);
        if (u == null) { System.out.println("No such user: " + name); return; }
        try (OutputStream os = Files.newOutputStream(u.in, StandardOpenOption.WRITE)) {
            os.write((msg + "\n").getBytes());
            os.flush();
        }
        System.out.println("Simulated message sent.");
    }

    private void startUserWatcher(User u) {
        u.running = true;
        Thread t = new Thread(() -> {
            while (u.running) {
                try (InputStream is = Files.newInputStream(u.in, StandardOpenOption.READ);
                     BufferedReader r = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    while ((line = r.readLine()) != null) {
                        handleUserMessage(u, line);
                    }
                } catch (IOException e) {
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                }
            }
        }, "watcher-" + u.name);
        t.setDaemon(true);
        t.start();
    }

    private void handleUserMessage(User u, String message) {
        // notify external listener about incoming user message
        onMessageReceived(u.name, message);
    }

    private void createFifoIfMissing(Path p) throws IOException, InterruptedException {
        if (Files.exists(p)) return;
        ProcessBuilder pb = new ProcessBuilder("mkfifo", p.toString());
        Process proc = pb.start();
        int rc = proc.waitFor();
        if (rc != 0) throw new IOException("mkfifo failed for " + p);
    }

    /**
     * Create a small helper script that opens tail -f on the out FIFO and provides a prompt
     * to send lines into the in FIFO. Then try to open it in a terminal emulator.
     */
    private void openTerminalForUser(User u) {
        Path script = null;
        try {
            script = createSessionScript(u);
        } catch (IOException ex) {
            System.out.println("(notice) couldn't create session script: " + ex.getMessage());
        }
        String scriptPath = (script != null) ? script.toAbsolutePath().toString() : null;

        String[][] candidates = new String[][]{
            {"gnome-terminal", "--", "bash", "-ic", scriptPath != null ? scriptPath : "bash"},
            {"konsole", "-e", "bash", "-ic", scriptPath != null ? scriptPath : "bash"},
            {"xterm", "-e", "bash", "-ic", scriptPath != null ? scriptPath : "bash"},
            {"xfce4-terminal", "-e", "bash", "-ic", scriptPath != null ? scriptPath : "bash"},
            {"mate-terminal", "-e", "bash", "-ic", scriptPath != null ? scriptPath : "bash"},
            {"alacritty", "-e", "bash", "-ic", scriptPath != null ? scriptPath : "bash"}
        };

        for (String[] cmd : candidates) {
            try {
                new ProcessBuilder(cmd).start();
                System.out.println("Opened terminal for user: " + u.name);
                return;
            } catch (IOException e) {
                // try next
            }
        }
        if (u.out != null) System.out.println("(notice) No supported terminal emulator found. Read replies with: cat " + u.out.toAbsolutePath());
    }

    private Path createSessionScript(User u) throws IOException {
        String inPath = u.in.toAbsolutePath().toString();
        String outPath = u.out.toAbsolutePath().toString();
        Path script = FIFO_DIR.resolve(u.name + "_session.sh");
        String content = "#!/bin/bash\n" +
                "set -e\n" +
                "trap 'kill $TAILPID 2>/dev/null; exit' EXIT INT TERM\n" +
                "# ensure FIFOs exist\n" +
                "while [ ! -p \"" + inPath + "\" ]; do sleep 0.1; done\n" +
                "while [ ! -p \"" + outPath + "\" ]; do sleep 0.1; done\n" +
                "echo 'Terminal connected to bot. Type lines to send. Ctrl-C to exit.'\n" +
                "tail -n +1 -f \"" + outPath + "\" &\n" +
                "TAILPID=$!\n" +
                "while true; do\n" +
                "  read -e -p 'msg> ' LINE || break\n" +
                "  # write to the bot input FIFO\n" +
                "  echo \"$LINE\" > \"" + inPath + "\"\n" +
                "done\n" +
                "kill $TAILPID 2>/dev/null || true\n";
        Files.writeString(script, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        try {
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
            Files.setPosixFilePermissions(script, perms);
        } catch (UnsupportedOperationException ignored) {
            // ignore systems without POSIX permissions
        }
        return script;
    }

    private static class User {
        final String name;
        final Path in;
        final Path out;
        volatile boolean running = false;
        User(String name, Path in, Path out) { this.name = name; this.in = in; this.out = out; }
        void stop() { running = false; }
    }
}

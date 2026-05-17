package GamieBot.infra.terminal;

import java.io.*;
import java.nio.channels.SocketChannel;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.net.UnixDomainSocketAddress;
import java.net.StandardProtocolFamily;

public class TerminalClient {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: TerminalClient <name>");
            System.exit(2);
        }
        String name = args[0];
        Path socket = Paths.get("infra/terminal/sockets/gamiebot.sock");

        SocketChannel ch = SocketChannel.open(StandardProtocolFamily.UNIX);
        UnixDomainSocketAddress addr = UnixDomainSocketAddress.of(socket);
        ch.connect(addr);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader r = new BufferedReader(new InputStreamReader(Channels.newInputStream(ch), StandardCharsets.UTF_8));
             BufferedWriter w = new BufferedWriter(new OutputStreamWriter(Channels.newOutputStream(ch), StandardCharsets.UTF_8))) {

            // introduce
            w.write("HELLO " + name + "\n");
            w.flush();

            Thread readerThread = new Thread(() -> {
                try {
                    String ln;
                    while ((ln = r.readLine()) != null) {
                        System.out.println(ln);
                    }
                } catch (IOException e) { /* exit */ }
            }, "socket-reader");
            readerThread.setDaemon(true);
            readerThread.start();

            String line;
            while ((line = in.readLine()) != null) {
                w.write(line + "\n");
                w.flush();
            }
        } finally {
            ch.close();
        }
    }
}

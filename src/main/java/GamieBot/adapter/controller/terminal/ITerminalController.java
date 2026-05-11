package GamieBot.adapter.controller.terminal;

/**
 * Interface for receiving messages from the ChatEmulator.
 */
public interface ITerminalController {
    void onMessageReceived(String user, String message);
}

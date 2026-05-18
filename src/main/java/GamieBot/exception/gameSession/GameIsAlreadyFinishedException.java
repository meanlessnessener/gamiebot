package GamieBot.exception.gameSession;

public class GameIsAlreadyFinishedException extends RuntimeException {

    public GameIsAlreadyFinishedException() {
        super();
    }

    public GameIsAlreadyFinishedException(String message) {
        super(message);
    }

    public GameIsAlreadyFinishedException(String message, Throwable cause) {
        super(message, cause);
    }
}
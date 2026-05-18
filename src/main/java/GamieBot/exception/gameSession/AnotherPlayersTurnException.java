package GamieBot.exception.gameSession;

public class AnotherPlayersTurnException extends RuntimeException {

    public AnotherPlayersTurnException() {
        super();
    }

    public AnotherPlayersTurnException(String message) {
        super(message);
    }

    public AnotherPlayersTurnException(String message, Throwable cause) {
        super(message, cause);
    }
}
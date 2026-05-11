package GamieBot.domain.games;

public class GameFactory {
    public static IGame createGame(String gameName) {
        return switch (gameName) {
            case "TicTacToe" -> new TicTacToe();
            case "Nim" -> new Nim();
            default -> null;
        };
    }
}
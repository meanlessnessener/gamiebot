package GamieBot.domain.games;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GameFactory {
    private static final HashMap<String, Supplier<IGame>> availableGames = new HashMap<>(Map.of(
        "TicTacToe", () -> new TicTacToe(),
        "Nim", () -> new Nim()
    ));

    public static boolean doesGameExist(String gameName) {
        return availableGames.containsKey(gameName);
    }
    
    public static IGame createGame(String gameName) {
        Supplier<IGame> gameCreator = availableGames.get(gameName);
        if (gameCreator == null) {
            return null;
        }
        return gameCreator.get();
    }
}
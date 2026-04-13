package GamieBot.model.games;

import java.util.ArrayList;
import java.util.Arrays;

public class GameManager {
    private static ArrayList<String> availableGames = new ArrayList<>(Arrays.asList("TicTacToe"));

    public static boolean doGameNameExists(String gameName) {
        return availableGames.contains(gameName);
    }

    public static IGame createGame(String gameName) throws Exception {
        if (!doGameNameExists(gameName)) {
            return null;
        }
        if (gameName == "TicTacToe") {
            return new TicTacToe();
        }
        throw new Exception();
    }

    public static ArrayList<String> getAvailableGames() {
        return availableGames;
    }
}

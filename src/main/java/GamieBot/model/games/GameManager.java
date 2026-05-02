package GamieBot.model.games;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameManager {
    private static final Logger log = LoggerFactory.getLogger(GameManager.class);
    private static ArrayList<String> availableGames = new ArrayList<>(Arrays.asList("TicTacToe"));

    public static boolean doGameNameExists(String gameName) {
        return availableGames.contains(gameName);
    }

    public static IGame createGame(String gameName) {
        log.info("Creating game '{}'", gameName);

        if (!doGameNameExists(gameName)) {
            log.info("Returning null 1");
            return null;
        }
        if (gameName.equals("TicTacToe")) {
            log.info("Returning TicTacToe");
            return new TicTacToe();
        }
        log.info("Returning null 2");
        return null;
    }

    public static ArrayList<String> getAvailableGames() {
        return availableGames;
    }
}

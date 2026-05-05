package GamieBot.model;

import java.util.ArrayList;

import GamieBot.domain.games.IGame;
import GamieBot.domain.user.User;

public class GameSession {
    private final IGame game;
    private final List<String> players;

    public GameSession(IGame game, List<String> users) {
        this.game = game;
        this.game.initGame();
        this.players = users;
    }

    private int getPlayerNum(String id) throws Exception {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).equals(id)) {
                return i;
            }
        }
        throw new Exception("Player not found");
    }

    public boolean makeMove(String id, String action) throws Exception {
        int playerNum = getPlayerNum(id);

        if (game.checkMove(playerNum, action)) {
            game.makeMove(playerNum, action);
            return true;
        } else {
            return false;
        }
    }

    public String getGameStateForPlayer(String id) throws Exception {
        int playerNum = getPlayerNum(id);

        return game.getInfoForPlayer(playerNum);
    }

    public boolean isFinished() {
        return game.isFinished();
    }

    public List<String> getPlayers() {
        return players;
    }
}

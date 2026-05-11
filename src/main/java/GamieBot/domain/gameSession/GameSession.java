package GamieBot.domain.gameSession;

import java.util.List;
import java.util.UUID;

import GamieBot.domain.games.IGame;

public class GameSession {
    private final IGame game;
    private final List<UUID> players;

    public GameSession(IGame game, List<UUID> users) {
        this.game = game;
        this.game.initGame();
        this.players = users;
    }

    private int getPlayerNum(UUID id) throws Exception {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).equals(id)) {
                return i;
            }
        }
        throw new Exception("Player not found");
    }

    public boolean makeMove(UUID id, String action) throws Exception {
        int playerNum = getPlayerNum(id);

        if (game.checkMove(playerNum, action)) {
            game.makeMove(playerNum, action);
            return true;
        } else {
            return false;
        }
    }

    public String getGameStateForPlayer(UUID id) throws Exception {
        int playerNum = getPlayerNum(id);

        return game.getInfoForPlayer(playerNum);
    }

    public boolean isFinished() {
        return game.isFinished();
    }

    public List<UUID> getPlayers() {
        return players;
    }
}

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

    private int getPlayerNum(UUID id) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public String makeMove(UUID id, String action) {
        int playerNum = getPlayerNum(id);

        if (playerNum == -1) {
            return "Такого игрока в сессии нет";
        }

        if (game.checkMove(playerNum, action)) {
            game.makeMove(playerNum, action);
            return "Ход выполнен";
        } else {
            return "Недопустимый ход";
        }
    }

    public String getGameStateForPlayer(UUID id) {
        int playerNum = getPlayerNum(id);

        if (playerNum == -1) {
            return "Такого игрока в сессии нет";
        }

        return game.getInfoForPlayer(playerNum);
    }

    public boolean isFinished() {
        return game.isFinished();
    }

    public List<UUID> getPlayers() {
        return players;
    }
}

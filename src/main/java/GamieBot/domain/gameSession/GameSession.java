package GamieBot.domain.gameSession;

import GamieBot.exception.gameSession.*;
import GamieBot.domain.games.IGame;
import java.util.List;
import java.util.UUID;

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

    public void makeMove(UUID id, String action) throws
        PlayerNotFoundException,
        GameIsAlreadyFinishedException,
        AnotherPlayersTurnException,
        InvalidMoveException 
    {
        int playerNum = getPlayerNum(id);

        if (playerNum == -1) {
            throw new PlayerNotFoundException();
        }

        if (game.isFinished()) {
            throw new GameIsAlreadyFinishedException();
        }

        if (playerNum != game.getMovingPlayer()) {
            throw new AnotherPlayersTurnException();
        }

        if (!game.checkMove(playerNum, action)) {
            throw new InvalidMoveException();
        }

        game.makeMove(playerNum, action);
    }

    public String getGameStateForPlayer(UUID id)
        throws PlayerNotFoundException {
        int playerNum = getPlayerNum(id);

        if (playerNum == -1) {
            throw new PlayerNotFoundException();
        }

        return game.getInfoForPlayer(playerNum);
    }

    public void capitulate(UUID id) throws PlayerNotFoundException {
        int playerNum = getPlayerNum(id);

        if (playerNum == -1) {
            throw new PlayerNotFoundException();
        }

        game.capitulate(playerNum);
    }

    public boolean isFinished() {
        return game.isFinished();
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public UUID getMovingPlayer() {
        return players.get(game.getMovingPlayer());
    }
}

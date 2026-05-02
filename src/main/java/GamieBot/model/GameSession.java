package GamieBot.model;

import java.util.ArrayList;

import GamieBot.model.games.IGame;
import GamieBot.model.users.User;

public class GameSession {
    private final IGame game;
    private final ArrayList<User> users;

    public GameSession(IGame game, ArrayList<User> users) {
        this.game = game;
        this.game.initGame();
        this.users = users;
    }

    private int getPlayerNum(String chatId) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).chatId.equals(chatId)) {
                return i;
            }
        }
        return -1;
    }

    public boolean makeMove(String chatId, String action) throws Exception {
        int playerNum = getPlayerNum(chatId);
        if (playerNum == -1) {
            throw new Exception("Player not found");
        }
        if (game.checkMove(playerNum, action)) {
            game.makeMove(playerNum, action);
            return true;
        } else {
            return false;
        }
    }

    public String getGameStateForUser(String chatId) {
        int playerNum = getPlayerNum(chatId);
        if (playerNum == -1) {
            return "Ты не в игре";
        }
        return game.getInfoForPlayer(playerNum);
    }

    public boolean isFinished() {
        return game.isFinished();
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}

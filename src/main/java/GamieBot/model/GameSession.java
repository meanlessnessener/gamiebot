package GamieBot.model;

import java.util.ArrayList;

import GamieBot.model.games.IGame;
import GamieBot.model.users.User;

public class GameSession {
    private final IGame game;
    private final ArrayList<User> users;

    public GameSession(IGame game, ArrayList<User> users) {
        this.game = game;
        this.users = users;
    }

    public boolean makeMove(User player, String action) {
        int playerNum = -1;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i) == player) {
                playerNum = i;
                break;
            }
        }
        if (game.checkMove(playerNum, action)) {
            game.makeMove(playerNum, action);
            return false;
        } else {
            return true;
        }
    }

    public ArrayList<Response> getInfoForPlayers() {
        ArrayList<Response> ans = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            ans.add(new Response(users.get(i).chatId(), game.getInfoForPlayer(i)));
        }
        return ans;
    }
}

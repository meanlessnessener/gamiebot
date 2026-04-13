package GamieBot.presenter.requestHandling.game;

import java.util.ArrayList;
import java.util.HashMap;

import GamieBot.model.GameSession;
import GamieBot.model.users.User;
import GamieBot.model.users.UserManager;
import GamieBot.model.users.UserStatus;
import GamieBot.model.games.GameManager;

public class SessionManager {
    private final HashMap<String, GameSession> sessions;
    private final UserManager users;

    public SessionManager(UserManager users) {
        sessions = new HashMap<>();
        this.users = users;
    }

    public void createSession(ArrayList<User> players, String gameName) {
        GameSession session = new GameSession(GameManager.createGame(gameName), players);
        for (User user : players) {
            users.getUser(user.chatId).status = UserStatus.INGAME;
            sessions.put(user.chatId, session);
        }
    }

    public void makeMove(String chatId, String action) {
        GameSession session = sessions.get(chatId);
        if (session != null) {
            session.makeMove(chatId, action);
            if (session.isFinished()) {
                for (User user : session.getUsers()) {
                    users.getUser(user.chatId).status = UserStatus.INMENU;
                    sessions.remove(user.chatId);
                }
            }
        }
    }
}

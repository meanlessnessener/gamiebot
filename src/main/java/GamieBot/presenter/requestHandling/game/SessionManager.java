package GamieBot.presenter.requestHandling.game;

import java.util.HashMap;

import GamieBot.model.GameSession;
import GamieBot.model.users.User;
import GamieBot.model.users.UserManager;

public class SessionManager {
    private final HashMap<String, GameSession> sessions;
    private final UserManager users;

    public SessionManager(UserManager users) {
        sessions = new HashMap<>();
        this.users = users;
    }

    public void createSession(ArrayList<User> users, String gameName) {
        GameSession session = new GameSession(GameManager.createGame(gameName), users);
        for (User user : users) {
            this.users.setUserStatus(user.chatId, UserStatus.IN_GAME);
            sessions.put(user.chatId, session);
        }
    }

    public void makeMove(String chatId, String action) {
        GameSession session = sessions.get(chatId);
        if (session != null) {
            boolean ok = session.makeMove(chatId, action);
            if (session.isFinished()) {
                for (User user : session.getUsers()) {
                    this.users.setUserStatus(user.chatId, UserStatus.IDLE);
                    sessions.remove(user.chatId);
                }
            }
        }
    }
}

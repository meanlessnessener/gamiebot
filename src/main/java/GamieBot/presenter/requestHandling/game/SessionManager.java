package GamieBot.presenter.requestHandling.game;

import java.util.HashMap;

import GamieBot.model.GameSession;
import GamieBot.model.users.User;
import GamieBot.model.users.UserManager;

public class SessionManager {
    private final HashMap<User, GameSession> sessions;
    private final UserManager users;

    public SessionManager(UserManager users) {
        sessions = new HashMap<>();
        this.users = users;
    }

}

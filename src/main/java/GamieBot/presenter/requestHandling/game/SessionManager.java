package GamieBot.presenter.requestHandling.game;

import java.util.ArrayList;
import java.util.HashMap;

import GamieBot.model.GameSession;
import GamieBot.model.users.User;
import GamieBot.model.users.UserManager;
import GamieBot.model.users.UserStatus;
import GamieBot.model.games.GameManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager {
    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);

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
        log.info("Session is created for players: {}", players.stream().map(u -> u.chatId).toList());
    }

    public boolean makeMove(String chatId, String action) throws Exception {
        GameSession session = sessions.get(chatId);
        if (session != null) {
            log.info("The player {} made action {}", chatId, action);
            return session.makeMove(chatId, action);
        } else {
            log.warn("Session is not found for chatId {}", chatId);
            throw new Exception("Session not found for chatId: " + chatId);
        }
    }

    public String getGameStateForUser(String chatId) {
        GameSession session = sessions.get(chatId);
        if (session != null) {
            return session.getGameStateForUser(chatId);
        }
        log.warn("Session is not found for chatId {}", chatId);
        return "Ты не в игре";
    }

    public ArrayList<User> getPlayersInSession(String chatId) {
        GameSession session = sessions.get(chatId);
        if (session != null) {
            return session.getUsers();
        }
        log.warn("Session is not found for chatId {}", chatId);
        return new ArrayList<>();
    }

    public void removeSession(String chatId) {
        GameSession session = sessions.get(chatId);
        if (session != null) {
            for (User user : session.getUsers()) {
                users.getUser(user.chatId).status = UserStatus.INMENU;
                sessions.remove(user.chatId);
            }
        }
        log.info("Session removed for chatId: {}", chatId);
}
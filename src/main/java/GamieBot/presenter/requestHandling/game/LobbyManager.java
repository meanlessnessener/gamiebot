package GamieBot.presenter.requestHandling.game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import GamieBot.model.games.GameManager;
import GamieBot.model.users.User;
import GamieBot.model.users.UserManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LobbyManager {
    private static final Logger log = LoggerFactory.getLogger(LobbyManager.class);
    private final UserManager users;
    private HashMap<String, ArrayDeque<String>> waitList;

    public LobbyManager(UserManager users) {
        this.users = users;
        waitList = new HashMap<>();
        
        for (String gameName : GameManager.getAvailableGames()) {
            waitList.put(gameName, new ArrayDeque<String>());
        }
    }

    public void addUserToLobby(String chatId, String gameName) {
        waitList.get(gameName).addLast(chatId);
        log.info("User added to lobby for game {}: {}", gameName, chatId);
    }

    public boolean isLobbyFull(String gameName) {
        return waitList.get(gameName).size() >= 2;
    }

    public ArrayList<User> getUsersForNewSession(String gameName) {
        ArrayList<User> res = new ArrayList<>();
        
        for (int i = 0; i < 2; i++) {
            String chatId = waitList.get(gameName).getFirst();
            waitList.get(gameName).removeFirst();
            res.add(users.getUser(chatId));
            log.info("User removed from lobby for game {}: {}", gameName, chatId);
        }

        return res;
    }
}

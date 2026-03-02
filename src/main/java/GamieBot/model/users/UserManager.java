package GamieBot.model.users;

import java.util.HashMap;


public class UserManager {
    private final HashMap<String, User> users;

    public UserManager() {
        users = new HashMap<>();
    }

    public void insert(String chatId, User user) {
        users.put(chatId, user);
    }

    public User get(String chatId) {
        return users.getOrDefault(chatId, null);
    }

    public void erase(String chatId) {
        users.remove(chatId);
    }

    public int size() {
        return users.size();
    }

    public void clear() {
        users.clear();
    }
}

package GamieBot.model.users;

import java.util.HashMap;

public class UserManager implements IUserManager {
    private final HashMap<String, User> users;

    public UserManager() {
        users = new HashMap<>();
    }

    @Override
    public void registerNewUser(String chatId) {
        users.put(chatId, new User(chatId));
    }

    @Override
    public User getUser(String chatId) {
        if (users.containsKey(chatId)) {
            return users.get(chatId);
        } else {
            throw null;
        }
    }

    @Override
    public boolean isUserRegistered(String chatId) {
        return users.containsKey(chatId);
    }
}

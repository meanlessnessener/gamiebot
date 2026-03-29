package GamieBot.model.users;

import java.util.HashMap;

public class UserManager implements IUserManager {
    private final HashMap<String, User> users;

    public UserManager() {
        users = new HashMap<>();
    }

    @Override
    public void registerNewUser(String chatId) {
        users.put(chatId, new User(chatId, UserStatus.INMENU));
    }

    @Override
    public User getUser(String chatId) throws Exception {
        if (users.containsKey(chatId)) {
            return users.get(chatId);
        } else {
            throw new Exception();
        }
    }
}

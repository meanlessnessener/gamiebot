package GamieBot.model.users;

public class User {
    public String chatId;
    public UserStatus status;

    User(String chatId) {
        this.chatId = chatId;
        status = UserStatus.INMENU;
    }
}

package GamieBot.model.users;

public interface IUserManager {
    public void registerNewUser(String chatId);

    public User getUser(String chatId) throws Exception;

    public boolean isUserRegistered(String chatId);
}

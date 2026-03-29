package GamieBot.presenter;

public interface IEventListener {
    public void onMessageReceived(String chatId, String text);
}

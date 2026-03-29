package GamieBot.view;

import GamieBot.presenter.IEventListener;

public interface IView {
    public void setListener(IEventListener listener);

    public void sendMessage(String chatId, String text);

    public void run();
}

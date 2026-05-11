package GamieBot.adapter.presenter;

import java.util.UUID;

public interface IPresenter {
    public void sendMessage(UUID chatId, String msg);    
}
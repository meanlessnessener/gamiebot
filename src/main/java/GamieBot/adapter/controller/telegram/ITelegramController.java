package GamieBot.adapter.controller.telegram;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface ITelegramController {
    public void onUpdateReceived(Message msg);
}

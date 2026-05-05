package GamieBot.adapter.controller;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ITelegramController {
    public void onUpdateReceived(Update update);
}

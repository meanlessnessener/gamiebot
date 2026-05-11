package GamieBot.adapter.presenter;

import GamieBot.infra.telegram.TelegramBot;
import GamieBot.domain.user.User;
import GamieBot.infra.repo.user.IUserRepo;

import java.util.UUID;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class TelegramPresenter implements IPresenter {
    private final TelegramBot telegramBot;
    private final IUserRepo userRepo;
    
    public TelegramPresenter(TelegramBot telegramBot, IUserRepo userRepo) {
        this.telegramBot = telegramBot;
        this.userRepo = userRepo;
    }
    
    @Override
    public void sendMessage(UUID userId, String text) {
        SendMessage message = new SendMessage();
        message.setParseMode("MarkdownV2");
        text = "`" + text + "`";
        message.setText(text);

        User user = userRepo.getUserByUUID(userId);
        String chatId = user.getProviderToken("telegram");
        
        message.setChatId(chatId);
        telegramBot.sendMessage(message);
    }
}
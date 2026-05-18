package GamieBot.infra.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import io.github.cdimascio.dotenv.Dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import GamieBot.adapter.controller.telegram.ITelegramController;

public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramBot.class);
    private ITelegramController controller;
    private final Dotenv dotenv;

    public TelegramBot() {
        super();
        log.info("TelegramBot initialized");
        this.dotenv = Dotenv.load();
    }

    public void setController(ITelegramController controller) {
        this.controller = controller;
        log.info("Controller set: {}", controller.getClass().getName());
    }

    public void onUpdateReceived(Update update) {
        controller.onUpdateReceived(update.getMessage());
    }

    public void sendMessage(SendMessage message) {
        try {
            execute(message);
            log.info("Message sent to chatId {}: {}", message.getChatId(), message.getText());
        } catch (TelegramApiException e) {
            log.error("Error occurred while sending message: ", e);
        }
    }

    public void run() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            log.info("TelegramBot registered successfully");
        } catch (TelegramApiException e) {
            log.error("Error occurred while registering bot", e);
        }
    }

    @Override
    public String getBotUsername() {
        return "GamieBot";
    }

    @Override
    public String getBotToken() {
        String token = dotenv.get("TELEGRAM_BOT_TOKEN");
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("TELEGRAM_BOT_TOKEN not found");
        }
        return token;
    }
}

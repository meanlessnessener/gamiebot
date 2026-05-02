package GamieBot.view;

import GamieBot.presenter.IEventListener;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class TelegramBotView extends TelegramLongPollingBot implements IView {
    private static final Logger log = LoggerFactory.getLogger(TelegramBotView.class);

    private IEventListener listener;

    @Override
    public void setListener(IEventListener listener) {
        this.listener = listener;
        log.info("Listener set: {}", listener.getClass().getName());
    }

    @Override
    public void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
            log.info("Message sent to chatId {}: {}", chatId, text);
        } catch (TelegramApiException e) {
            log.error("Error occurred while sending message", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String text = update.getMessage().getText();
        listener.onMessageReceived(chatId, text);
    }

    @Override
    public void run() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
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
        return "8600850077:AAFslm-1R7ImBLVz0z_uR6NXDhY2v-qgR2I";
    }
}

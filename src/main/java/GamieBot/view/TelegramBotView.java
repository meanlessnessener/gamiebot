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

import java.util.ArrayList;
import java.util.Arrays;

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
        message.setParseMode("HTML");

        text = "<pre>" + text + "</pre>";

        // message.setChatId(chatId);
        message.setChatId("433165830");
        message.setText(String.join("::", chatId, text));

        try {
            execute(message);
            log.info("Message sent to chatId {}: {}", chatId, text);
        } catch (TelegramApiException e) {
            log.error("Error occurred while sending message", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        // String text = update.getMessage().getText();
        // String chatId = update.getMessage().getChatId().toString();
        ArrayList<String> msg = new ArrayList<>(Arrays.asList(update.getMessage().getText().split(" ")));

        String chatId = msg.get(0);
        msg.remove(0);
        String text = String.join(" ", msg);

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

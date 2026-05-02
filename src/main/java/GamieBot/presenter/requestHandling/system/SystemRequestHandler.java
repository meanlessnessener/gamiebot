package GamieBot.presenter.requestHandling.system;

import GamieBot.model.Response;
import GamieBot.presenter.requestHandling.IRequestHandler;
import GamieBot.model.users.UserManager;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemRequestHandler implements IRequestHandler {
    private static final Logger log = LoggerFactory.getLogger(SystemRequestHandler.class);

    private final UserManager users;

    public SystemRequestHandler(UserManager users) {
        this.users = users;
    }

    private final String startText = "Привет! Введи /help, чтобы получить справку";
    private final String helpText = "/help -- вывод справки\n\nБольше пока ничего нет :(";
    private final String[] commands = { "/start", "/help" };

    @Override
    public boolean shouldRequestBeHandledHere(String chatId, String text) {
        for (String command : commands) {
            if (text.startsWith(command)) {
                log.info("System command should be handled here for chatId {}: {}", chatId, text);
                return true;
            }
        }
        log.info("System command shouldn't be handled here for chatId {}: {}", chatId, text);
        return false;
    }

    @Override
    public ArrayList<Response> handleRequest(String chatId, String text) throws Exception {
        if (text.startsWith("/start")) {
            log.info("Handling /start command for chatId {}: {}", chatId, text);
            return handleStart(chatId);
        } else if (text.startsWith("/help")) {
            log.info("Handling /help command for chatId {}: {}", chatId, text);
            return handleHelp(chatId);
        }
        throw new Exception("Unknown system command: " + text);
    }

    private ArrayList<Response> handleStart(String chatId) {
        
        users.registerNewUser(chatId);
        log.info("Registered new user with chatId {}", chatId);
        Response response = new Response(chatId, startText);
        return new ArrayList<>(Arrays.asList(response));
    }

    private ArrayList<Response> handleHelp(String chatId) {
        users.registerNewUser(chatId);
        log.info("Handling /help command for chatId {}", chatId);
        Response response = new Response(chatId, helpText);
        return new ArrayList<>(Arrays.asList(response));
    }
}

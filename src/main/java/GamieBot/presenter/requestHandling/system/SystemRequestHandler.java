package GamieBot.presenter.requestHandling.system;

import GamieBot.model.Response;
import GamieBot.presenter.requestHandling.IRequestHandler;
import GamieBot.model.users.UserManager;
import java.util.ArrayList;
import java.util.Arrays;

public class SystemRequestHandler implements IRequestHandler {
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
            if (text.startsWith(command))
                return true;
        }
        return false;
    }

    @Override
    public ArrayList<Response> handleRequest(String chatId, String text) throws Exception {
        if (text.startsWith("/start")) {
            return handleStart(chatId);
        } else if (text.startsWith("/help")) {
            return handleHelp(chatId);
        }
        throw new Exception();
    }

    private ArrayList<Response> handleStart(String chatId) {
        users.registerNewUser(chatId);
        Response response = new Response(chatId, startText);
        return new ArrayList<>(Arrays.asList(response));
    }

    private ArrayList<Response> handleHelp(String chatId) {
        users.registerNewUser(chatId);
        Response response = new Response(chatId, helpText);
        return new ArrayList<>(Arrays.asList(response));
    }
}

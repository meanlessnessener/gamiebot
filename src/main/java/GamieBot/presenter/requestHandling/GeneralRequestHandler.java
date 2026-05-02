package GamieBot.presenter.requestHandling;

import GamieBot.presenter.requestHandling.game.GameRequestHandler;
import GamieBot.presenter.requestHandling.system.SystemRequestHandler;
import GamieBot.model.Response;
import GamieBot.model.users.UserManager;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneralRequestHandler implements IRequestHandler {
    private static final Logger log = LoggerFactory.getLogger(GeneralRequestHandler.class);

    private final GameRequestHandler gameRequestHandler;
    private final SystemRequestHandler systemRequestHandler;
    private final UserManager users;

    public GeneralRequestHandler() {
        users = new UserManager();
        gameRequestHandler = new GameRequestHandler(users);
        systemRequestHandler = new SystemRequestHandler(users);
    }

    private final String commandNotFoundText = "Такой команды нет. Введи /help, чтобы посмотреть список доступных команд";

    @Override
    public ArrayList<Response> handleRequest(String chatId, String text) {
        try {
            if (systemRequestHandler.shouldRequestBeHandledHere(chatId, text)) {
                log.info("Handling system request for chatId {}: {}", chatId, text);
                return systemRequestHandler.handleRequest(chatId, text);
            }
            if (gameRequestHandler.shouldRequestBeHandledHere(chatId, text)) {
                log.info("Handling game request for chatId {}: {}", chatId, text);
                return gameRequestHandler.handleRequest(chatId, text);
            }

            Response response = new Response(chatId, commandNotFoundText);
            log.info("Command not found for chatId {}: {}", chatId, text);
            return new ArrayList<>(Arrays.asList(response));
        } catch (Exception e) {
            log.error("Error occurred while handling request", e);
            Response response = new Response(chatId, "Произошла ошибка при обработке запроса. Попробуйте позже.");
            return new ArrayList<>(Arrays.asList(response));
        }
    }

    @Override
    public boolean shouldRequestBeHandledHere(String chatId, String text) {
        // ловушка джокушкера и архитектуры системы
        // надо бы отдельный интерфейс для текущего класса сделать
        return true;
    }
}

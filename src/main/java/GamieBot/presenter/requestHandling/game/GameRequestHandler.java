package GamieBot.presenter.requestHandling.game;

import GamieBot.model.Response;
import GamieBot.model.games.GameManager;
import GamieBot.presenter.requestHandling.IRequestHandler;
import GamieBot.model.users.UserManager;
import GamieBot.model.users.UserStatus;
import GamieBot.model.users.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

public class GameRequestHandler implements IRequestHandler {
    private static final Logger log = LoggerFactory.getLogger(GameRequestHandler.class);

    private final LobbyManager lobbyManager;
    private final SessionManager sessionManager;
    private final UserManager users;
    private final String[] commands = { "/play", "/quit" };
    private final String textGameNotFound = "Такой игры не существует";
    private final String textSearchingGame = "Ищем игру...";
    private final String textInvite2Play = "Нашлась игра!";

    public GameRequestHandler(UserManager users) {
        this.users = users;
        lobbyManager = new LobbyManager(users);
        sessionManager = new SessionManager(users);
    }

    @Override
    public ArrayList<Response> handleRequest(String chatId, String text) {

        if (text.startsWith("/play")) {
            log.info("Handling /play command for chatId {}: {}", chatId, text);
            return handlePlay(chatId, text);
        }

        if (text.startsWith("/quit")) {
            log.info("Handling /quit command for chatId {}: {}", chatId, text);
            return handleQuit(chatId);
        }

        log.info("Handling game move for chatId {}: {}", chatId, text);
        return handleMove(chatId, text);
    }

    private ArrayList<Response> handleMove(String chatId, String text) {
        ArrayList<Response> responses = new ArrayList<>();

        try {
            if (sessionManager.makeMove(chatId, text) == false) {
                responses.add(new Response(chatId, "Недопустимый ход"));
                return responses;
            }
        } catch (Exception e) {
            responses.add(new Response(chatId, "Произошла ошибка при выполнении хода: " + e.getMessage()));
            log.warn("Error occurred while handling move for chatId {} and action {}: {}", chatId, text, e.getMessage());
            return responses;
        }

        for (User user : sessionManager.getPlayersInSession(chatId)) {
            String gameState = sessionManager.getGameStateForUser(user.chatId);
            responses.add(new Response(user.chatId, gameState));
        }
        return responses;
    }

    private ArrayList<Response> handleQuit(String chatId) {
        ArrayList<Response> responses = new ArrayList<>();
        sessionManager.removeSession(chatId);

        for (User user : sessionManager.getPlayersInSession(chatId)) {
            String text;
            if (user.chatId.equals(chatId)) {
                text = "Ты покинул игру";
            } else {
                text = "Игрок " + chatId + " покинул игру. Игра прервана";
            }
            responses.add(new Response(user.chatId, text));
        }

        log.info("User {} quited the game", chatId);

        return responses;
    }

    private ArrayList<Response> handlePlay(String chatId, String text) {
        String gameName = text.split(" ")[1];

        if (!GameManager.doGameNameExists(gameName)) {
            Response response = new Response(chatId, textGameNotFound);
            return new ArrayList<>(Arrays.asList(response));
        }

        lobbyManager.addUserToLobby(chatId, gameName);
        ArrayList<Response> responses = new ArrayList<>();
        responses.add(new Response(chatId, textSearchingGame));

        if (lobbyManager.isLobbyFull(gameName)) {
            ArrayList<User> players = lobbyManager.getUsersForNewSession(gameName);
            sessionManager.createSession(players, gameName);
            
            for (User user : players) {
                responses.add(new Response(user.chatId, textInvite2Play));
                responses.add(new Response(user.chatId, sessionManager.getGameStateForUser(user.chatId)));
            }
        }

        return responses;
    }

    @Override
    public boolean shouldRequestBeHandledHere(String chatId, String text) {
        if (!users.isUserRegistered(chatId))
            return false;

        for (String currentCommand : commands) {
            if (text.startsWith(currentCommand))
                return true;
        }

        User currentUser;

        try {
            currentUser = users.getUser(chatId);
        } catch (Exception e) {
            return false;
        }

        if (currentUser.status == UserStatus.INGAME) {
            return true;
        } else {
            return false;
        }
    }
}

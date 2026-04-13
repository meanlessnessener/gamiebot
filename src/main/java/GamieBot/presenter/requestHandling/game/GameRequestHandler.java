package GamieBot.presenter.requestHandling.game;

import GamieBot.model.Response;
import GamieBot.model.games.GameManager;
import GamieBot.presenter.requestHandling.IRequestHandler;
import GamieBot.model.users.UserManager;
import GamieBot.model.users.UserStatus;
import GamieBot.model.users.User;

import java.util.ArrayList;
import java.util.Arrays;

public class GameRequestHandler implements IRequestHandler {
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
        ArrayList<Response> responses = new ArrayList<>();

        if (text.startsWith("/play")) {
            responses.addAll(handlePlay(chatId, text));
        }

        return new ArrayList<>();
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
            }
        }

        return responses;
    }

    @Override
    public boolean shouldRequestBeHandledHere(String chatId, String text) {
        if (!users.isUserRegistered(chatId))
            return false;

        User currentUser;

        try {
            currentUser = users.getUser(chatId);
        } catch (Exception e) {
            return false;
        }

        for (String currentCommand : commands) {
            if (text.startsWith(currentCommand))
                return true;
        }

        if (currentUser.status == UserStatus.INGAME) {
            return true;
        } else {
            return false;
        }
    }
}

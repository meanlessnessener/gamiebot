package GamieBot.presenter.requestHandling.game;

import GamieBot.model.Response;
import GamieBot.presenter.requestHandling.IRequestHandler;
import GamieBot.model.users.UserManager;
import java.util.ArrayList;


public class GameRequestHandler implements IRequestHandler {
    private final LobbyManager lobbyManager;
    private final SessionManager sessionManager;
    private final UserManager users;

    public GameRequestHandler(UserManager users) {
        this.users = users;
        lobbyManager = new LobbyManager(users);
        sessionManager = new SessionManager(users);
    }

    @Override
    public ArrayList<Response> handleRequest(String chatId, String text) {
        return new ArrayList<>();
    }

    @Override
    public boolean shouldRequestBeHandledHere(String chatId, String text) {
        return false;
    }
}

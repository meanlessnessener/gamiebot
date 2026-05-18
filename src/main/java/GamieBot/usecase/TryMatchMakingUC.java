package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;
import GamieBot.adapter.resources.IMessageService;
import GamieBot.domain.gameSession.GameSession;
import GamieBot.domain.games.GameFactory;
import GamieBot.domain.games.IGame;
import GamieBot.infra.repo.lobby.ILobbyRepo;
import GamieBot.infra.repo.session.IGameSessionRepo;
import java.util.List;
import java.util.UUID;

public class TryMatchMakingUC {

    private final ILobbyRepo lobbyRepo;
    private final IGameSessionRepo gameSessionRepo;
    private final IPresenter presenter;
    private final IMessageService messageService;

    public TryMatchMakingUC(
        ILobbyRepo lobbyRepo,
        IGameSessionRepo gameSessionRepo,
        IPresenter presenter,
        IMessageService messageService
    ) {
        this.lobbyRepo = lobbyRepo;
        this.gameSessionRepo = gameSessionRepo;
        this.presenter = presenter;
        this.messageService = messageService;
    }

    public void execute(String gameName) {
        List<UUID> usersInLobby = lobbyRepo.getUsersInLobby(gameName, 2);
        if (usersInLobby == null || usersInLobby.size() < 2) {
            return;
        }
        
        IGame game = GameFactory.createGame(gameName);
        if (game == null) {
            return;
        }
        
        GameSession session = new GameSession(game, usersInLobby);
        gameSessionRepo.saveSession(session);
        
        String gameStarted = messageService.get("matchMaking.gameStarted", null);
        String yourMove = messageService.get("matchMaking.yourMove", null);
        String othersMove = messageService.get("matchMaking.othersMove", null);
        
        for (UUID userId : usersInLobby) {
            presenter.sendMessage(userId, gameStarted);
            presenter.sendMessage(userId, session.getGameStateForPlayer(userId));
            if (userId == session.getMovingPlayer()) {
                presenter.sendMessage(userId, yourMove);
            } else {
                presenter.sendMessage(userId, othersMove);
            }
        }
    }
}

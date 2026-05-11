package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;
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

    public TryMatchMakingUC(
        ILobbyRepo lobbyRepo,
        IGameSessionRepo gameSessionRepo,
        IPresenter presenter
    ) {
        this.lobbyRepo = lobbyRepo;
        this.gameSessionRepo = gameSessionRepo;
        this.presenter = presenter;
    }

    public void execute(String gameName) {
        List<UUID> usersInLobby = lobbyRepo.getUsersInLobby(gameName, 2);
        if (usersInLobby == null) {
            return;
        }
        if (usersInLobby.size() < 2) {
            return;
        }
        
        IGame game = GameFactory.createGame(gameName);
        if (game == null) {
            return;
        }
        
        GameSession session = new GameSession(game, usersInLobby);
        gameSessionRepo.saveSession(session);
        
        for (UUID userId : usersInLobby) {
            presenter.sendMessage(userId, "Найдена игра!");
            presenter.sendMessage(userId, session.getGameStateForPlayer(userId));
        }
    }
}

package GamieBot.usecase;

import GamieBot.infra.repo.lobby.ILobbyRepo;
import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.domain.games.GameFactory;
import GamieBot.domain.games.IGame;
import GamieBot.domain.user.User;
import GamieBot.adapter.presenter.IPresenter;
import java.util.UUID;

public class JoinLobbyUC {
    private final IUserRepo userRepo;
    private final ILobbyRepo lobbyRepo;
    private final IPresenter presenter;

    public JoinLobbyUC(IUserRepo userRepo, ILobbyRepo lobbyRepo, IPresenter presenter) {
        this.userRepo = userRepo;
        this.lobbyRepo = lobbyRepo;
        this.presenter = presenter;
    }

    public void execute(UUID id, String gameName) {
        User user = userRepo.getUserByUUID(id);
        if (user == null) {
            return;
        }
        if (gameName == null || gameName.isEmpty()) {
            presenter.sendMessage(id, "Укажите название игры");
            return;
        }
        IGame mockGame = GameFactory.createGame(gameName);
        if (mockGame == null) {
            presenter.sendMessage(id, "Игра " + gameName + " не найдена");
            return;
        }
        lobbyRepo.addUserToLobby(id, gameName);
        presenter.sendMessage(id, "Ищем игру в " + gameName);
    }
}

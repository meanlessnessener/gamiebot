package GamieBot.usecase;

import GamieBot.infra.repo.lobby.ILobbyRepo;
import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.domain.games.GameFactory;
import GamieBot.domain.games.IGame;
import GamieBot.domain.user.User;
import GamieBot.adapter.presenter.IPresenter;
import GamieBot.adapter.resources.IMessageService;
import java.util.UUID;

public class JoinLobbyUC {
    private final IUserRepo userRepo;
    private final ILobbyRepo lobbyRepo;
    private final IPresenter presenter;
    private final IMessageService messageService;

    public JoinLobbyUC(IUserRepo userRepo, ILobbyRepo lobbyRepo, IPresenter presenter, IMessageService messageService) {
        this.userRepo = userRepo;
        this.lobbyRepo = lobbyRepo;
        this.presenter = presenter;
        this.messageService = messageService;
    }

    public void execute(UUID id, String gameName) {
        User user = userRepo.getUserByUUID(id);
        if (user == null) {
            return;
        }
        if (gameName == null || gameName.isEmpty()) {
            String text = messageService.get("joinLobby.gameNameRequired", null);
            presenter.sendMessage(id, text);
            return;
        }
        if (!GameFactory.doesGameExist(gameName)) {
            String text = messageService.get("joinLobby.gameDoesNotExist", null, gameName);
            presenter.sendMessage(id, text);
            return;
        }
        lobbyRepo.addUserToLobby(id, gameName);
        String text = messageService.get("joinLobby.searchingGame", null, gameName);
        presenter.sendMessage(id, text);
    }
}

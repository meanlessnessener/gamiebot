package GamieBot.usecase;

import GamieBot.infra.repo.lobby.ILobbyRepo;
import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.adapter.presenter.IPresenter;
import GamieBot.adapter.resources.IMessageService;
import GamieBot.domain.user.User;
import GamieBot.domain.user.UserStatus;
import java.util.UUID;

public class QuitLobbyUC {
    private final IUserRepo userRepo;
    private final ILobbyRepo lobbyRepo;
    private final IPresenter presenter;
    private final IMessageService messageService;
    
    public QuitLobbyUC(IUserRepo userRepo, ILobbyRepo lobbyRepo, IPresenter presenter, IMessageService messageService) {
        this.userRepo = userRepo;
        this.lobbyRepo = lobbyRepo;
		this.presenter = presenter;
		this.messageService = messageService;
    }

    public void execute(UUID userId) {
        User user = userRepo.getUserByUUID(userId);
        if (user == null) {
            return;
        }
        if (user.getStatus() != UserStatus.SEARCHING) {
            String text = messageService.get("quitLobby.notInLobby", null);
            presenter.sendMessage(userId, text);
            return;
        }
        user.setStatus(UserStatus.IDLE);
        userRepo.saveUser(userId, user);
        
        String text = messageService.get("quitLobby.youLeftTheLobby", null);
        presenter.sendMessage(userId, text);
        lobbyRepo.removeUserFromLobby(userId);
    }
}
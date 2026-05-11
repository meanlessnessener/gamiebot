package GamieBot.usecase;

import GamieBot.infra.repo.lobby.ILobbyRepo;
import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.domain.user.User;
import java.util.UUID;

public class QuitLobbyUC {
    private final IUserRepo userRepo;
    private final ILobbyRepo lobbyRepo;
    
    public QuitLobbyUC(IUserRepo userRepo, ILobbyRepo lobbyRepo) {
        this.userRepo = userRepo;
        this.lobbyRepo = lobbyRepo;
    }

    public void execute(UUID userId) {
        User user = userRepo.getUserByUUID(userId);
        if (user == null) {
            return;
        }
        lobbyRepo.removeUserFromLobby(userId);
    }
}
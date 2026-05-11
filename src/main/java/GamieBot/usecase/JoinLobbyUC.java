package GamieBot.usecase;

import java.util.UUID;

import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.infra.repo.lobby.ILobbyRepo;

public class JoinLobbyUC {
    private final IUserRepo userRepo;
    private final ILobbyRepo lobbyRepo;

    public JoinLobbyUC(IUserRepo userRepo, ILobbyRepo lobbyRepo) {
        this.userRepo = userRepo;
        this.lobbyRepo = lobbyRepo;
    }
    
    public void execute(UUID user, String game) {
        
    }
}
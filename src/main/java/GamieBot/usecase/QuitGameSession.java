package GamieBot.usecase;

import GamieBot.domain.gameSession.GameSession;
import GamieBot.domain.user.User;
import GamieBot.infra.repo.session.IGameSessionRepo;
import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.adapter.presenter.IPresenter;

import java.util.UUID;

public class QuitGameSessionUC {
    private final IUserRepo userRepo;
    private final IGameSessionRepo gameSessionRepo;
    private final IPresenter presenter;
    
    public QuitGameSessionUC(IUserRepo userRepo, IGameSessionRepo gameSessionRepo, IPresenter presenter) {
        this.userRepo = userRepo;
        this.gameSessionRepo = gameSessionRepo;
        this.presenter = presenter;
    }

    public void execute(UUID userId) {
        User user = userRepo.getUserByUUID(userId);
        if (user == null) {
            return;
        }
        GameSession session = gameSessionRepo.getSessionByUserUUID(userId);
        if (session == null) {
            return;
        }

        presenter.sendMessage(userId, "Вы вышли из игры");
        for (UUID playerId : session.getPlayerIds()) {
            if (!playerId.equals(userId)) {
                presenter.sendMessage(playerId, "Игрок " + user.getName() + " вышел из игры");
            }
        }

        // todo: finish game session
        
        gameSessionRepo.saveSession(session);
    }
}
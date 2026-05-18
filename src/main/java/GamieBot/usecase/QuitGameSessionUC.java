package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;
import GamieBot.adapter.resources.IMessageService;
import GamieBot.domain.gameSession.GameSession;
import GamieBot.domain.user.User;
import GamieBot.domain.user.UserStatus;
import GamieBot.exception.gameSession.PlayerNotFoundException;
import GamieBot.infra.repo.session.IGameSessionRepo;
import GamieBot.infra.repo.user.IUserRepo;
import java.util.UUID;

public class QuitGameSessionUC {

    private final IUserRepo userRepo;
    private final IGameSessionRepo gameSessionRepo;
    private final IPresenter presenter;
    private final IMessageService messageService;

    public QuitGameSessionUC(
        IUserRepo userRepo,
        IGameSessionRepo gameSessionRepo,
        IPresenter presenter,
        IMessageService messageService
    ) {
        this.userRepo = userRepo;
        this.gameSessionRepo = gameSessionRepo;
        this.presenter = presenter;
        this.messageService = messageService;
    }

    public void execute(UUID userId) {
        User user = userRepo.getUserByUUID(userId);
        if (user == null) {
            return;
        }
        if (user.getStatus() != UserStatus.INGAME) {
            String text = messageService.get(
                "quitGameSession.notInGame",
                null
            );
            presenter.sendMessage(userId, text);
            return;
        }


        GameSession session = gameSessionRepo.getSessionByUserUUID(userId);
        if (session == null) {
            String text = messageService.get(
                "quitGameSession.sessionNotFound",
                null
            );
            presenter.sendMessage(userId, text);
            return;
        }

        try {
            session.capitulate(userId);

            for (UUID playerId : session.getPlayers()) {
                if (playerId.equals(userId)) {
                    String text = messageService.get(
                        "quitGameSession.youLeftTheGame",
                        null
                    );
                    presenter.sendMessage(playerId, text);
                } else {
                    String text = messageService.get(
                        "quitGameSession.playerLeftTheGame",
                        null
                    );
                    presenter.sendMessage(playerId, text);
                }
                
                User player = userRepo.getUserByUUID(playerId);
                player.setStatus(UserStatus.IDLE);
                userRepo.saveUser(playerId, player);
            }
        } catch (PlayerNotFoundException e) {
            String text = messageService.get(
                "quitGameSession.playerNotFound",
                null
            );
            presenter.sendMessage(userId, text);
        }

        gameSessionRepo.saveSession(session);
    }
}

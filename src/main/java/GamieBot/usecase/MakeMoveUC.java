package GamieBot.usecase;

import GamieBot.domain.gameSession.GameSession;
import GamieBot.infra.repo.session.IGameSessionRepo;
import java.util.UUID;

public class MakeMoveUC {
    private final IGameSessionRepo gameSessionRepo;

    public MakeMoveUC(IGameSessionRepo gameSessionRepo) {
        this.gameSessionRepo = gameSessionRepo;
    }

    public void execute(UUID userId, String move) {
        GameSession session = gameSessionRepo.getSessionByUserUUID(userId);
        if (session == null) {
            return;
        }
        String result = session.makeMove(userId, move);
        gameSessionRepo.saveSession(session);

        presenter.sendMessage(userId, result);

        for (UUID playerId : session.getPlayers()) {
            String gameState = session.getGameStateForPlayer(playerId);
            presenter.sendMessage(playerId, gameState);
        }
    }
}
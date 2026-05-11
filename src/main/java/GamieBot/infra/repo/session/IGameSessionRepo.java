package GamieBot.infra.repo.session;

import GamieBot.domain.gameSession.GameSession;
import java.util.UUID;

public interface IGameSessionRepo {
    void saveSession(GameSession session);
    GameSession getSessionByUserUUID(UUID userId);
}
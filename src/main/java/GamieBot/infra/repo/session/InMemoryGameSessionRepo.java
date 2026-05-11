package GamieBot.infra.repo.session;

import java.util.HashMap;
import java.util.UUID;

import GamieBot.domain.gameSession.GameSession;

public class InMemoryGameSessionRepo implements IGameSessionRepo {
    private final HashMap<UUID, GameSession> sessions = new HashMap<>();

    @Override
    public void saveSession(GameSession session) {
        for (UUID userId : session.getPlayers()) {
            sessions.put(userId, session);
        }
    }

    @Override
    public GameSession getSessionByUserUUID(UUID userId) {
        return sessions.get(userId);
    }
}
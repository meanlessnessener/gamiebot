package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;
import GamieBot.adapter.resources.TestMessageService;
import GamieBot.infra.repo.lobby.ILobbyRepo;
import GamieBot.infra.repo.session.IGameSessionRepo;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TryMatchMakingUCTest {
    static class CapturingPresenter implements IPresenter {
        public final Map<UUID, List<String>> msgs = new HashMap<>();
        @Override
        public void sendMessage(UUID chatId, String msg) {
            msgs.computeIfAbsent(chatId, k -> new ArrayList<>()).add(msg);
        }
    }

    static class FakeLobbyRepo implements ILobbyRepo {
        private final List<UUID> users;
        public FakeLobbyRepo(List<UUID> users) { this.users = users; }
        @Override public void addUserToLobby(UUID userId, String gameName) {}
        @Override public void removeUserFromLobby(UUID userId) {}
        @Override public List<UUID> getUsersInLobby(String gameName, int count) { return users; }
    }

    static class FakeGameSessionRepo implements IGameSessionRepo {
        public Object saved;
        @Override public void saveSession(GamieBot.domain.gameSession.GameSession session) { this.saved = session; }
        @Override public GamieBot.domain.gameSession.GameSession getSessionByUserUUID(UUID userId) { return null; }
    }

    @Test
    public void createsSessionAndNotifiesPlayers() {
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();
        List<UUID> users = Arrays.asList(u1, u2);

        CapturingPresenter presenter = new CapturingPresenter();
        FakeLobbyRepo lobbyRepo = new FakeLobbyRepo(users);
        FakeGameSessionRepo sessionRepo = new FakeGameSessionRepo();

        TryMatchMakingUC uc = new TryMatchMakingUC(null, lobbyRepo, sessionRepo, presenter, new TestMessageService());
        uc.execute("TicTacToe");

        assertNotNull(sessionRepo.saved, "Session should be saved");
        assertTrue(presenter.msgs.containsKey(u1));
        assertTrue(presenter.msgs.containsKey(u2));
        assertTrue(presenter.msgs.get(u1).stream().anyMatch(s -> s.contains("Game started!")));
    }
}

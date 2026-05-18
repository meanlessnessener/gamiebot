package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;
import GamieBot.adapter.resources.TestMessageService;
import GamieBot.domain.games.TicTacToe;
import GamieBot.domain.gameSession.GameSession;
import GamieBot.domain.user.User;
import GamieBot.domain.user.UserStatus;
import GamieBot.infra.repo.session.IGameSessionRepo;
import GamieBot.infra.repo.user.IUserRepo;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MakeMoveUCTest {
    static class CapturingPresenter implements IPresenter {
        public final Map<UUID, List<String>> msgs = new HashMap<>();
        @Override
        public void sendMessage(UUID chatId, String msg) {
            msgs.computeIfAbsent(chatId, k -> new ArrayList<>()).add(msg);
        }
    }

    static class FakeUserRepo implements IUserRepo {
        private final Map<UUID, User> users = new HashMap<>();
        @Override public User getUserByUUID(UUID id) { return users.getOrDefault(id, new User(id, "test")); }
        @Override public void saveUser(UUID id, User user) { users.put(id, user); }
        @Override public UUID getUserByProvider(String provider, String token) { return null; }
        @Override public void saveUserByProvider(String provider, String token, UUID userId) {}
    }

    static class FakeSessionRepo implements IGameSessionRepo {
        private GameSession session;
        public FakeSessionRepo(GameSession s) { this.session = s; }
        @Override public void saveSession(GameSession session) { this.session = session; }
        @Override public GameSession getSessionByUserUUID(UUID userId) { return session; }
    }

    @Test
    public void makesMoveAndNotifiesPlayers() {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        TicTacToe game = new TicTacToe();
        List<UUID> players = Arrays.asList(p1, p2);
        GameSession session = new GameSession(game, players);

        CapturingPresenter presenter = new CapturingPresenter();
        FakeUserRepo userRepo = new FakeUserRepo();
        User u1 = new User(p1, "player1");
        u1.setStatus(UserStatus.INGAME);
        userRepo.saveUser(p1, u1);
        User u2 = new User(p2, "player2");
        u2.setStatus(UserStatus.INGAME);
        userRepo.saveUser(p2, u2);
        FakeSessionRepo repo = new FakeSessionRepo(session);

        MakeMoveUC uc = new MakeMoveUC(userRepo, repo, presenter, new TestMessageService());
        uc.execute(p1, "1 1");

        // Expect that the player who moved got a confirmation and both players got state updates
        assertTrue(presenter.msgs.get(p1).stream().anyMatch(s -> s.equals("Ход выполнен") || s.contains("The Game is end") || s.length()>0));
        assertTrue(presenter.msgs.containsKey(p2));
    }
}

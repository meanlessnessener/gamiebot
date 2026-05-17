package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;
import GamieBot.domain.user.User;
import GamieBot.infra.repo.lobby.ILobbyRepo;
import GamieBot.infra.repo.user.IUserRepo;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JoinLobbyUCTest {
    static class TestPresenter implements IPresenter {
        public UUID lastId;
        public String lastMsg;
        @Override
        public void sendMessage(UUID chatId, String msg) {
            this.lastId = chatId;
            this.lastMsg = msg;
        }
    }

    static class FakeUserRepo implements IUserRepo {
        private final UUID id;
        public FakeUserRepo(UUID id) { this.id = id; }
        @Override public User getUserByUUID(UUID id) { return new User(id, "test"); }
        @Override public void saveUser(UUID id, User user) {}
        @Override public UUID getUserByProvider(String provider, String token) { return null; }
        @Override public void saveUserByProvider(String provider, String token, UUID userId) {}
    }

    static class FakeLobbyRepo implements ILobbyRepo {
        public UUID addedUser;
        public String addedGame;
        @Override public void addUserToLobby(UUID userId, String gameName) { this.addedUser = userId; this.addedGame = gameName; }
        @Override public void removeUserFromLobby(UUID userId) {}
        @Override public java.util.List<UUID> getUsersInLobby(String gameName, int count) { return null; }
    }

    @Test
    public void joinsLobbyWhenGameExists() {
        UUID id = UUID.randomUUID();
        TestPresenter presenter = new TestPresenter();
        FakeUserRepo userRepo = new FakeUserRepo(id);
        FakeLobbyRepo lobbyRepo = new FakeLobbyRepo();

        JoinLobbyUC uc = new JoinLobbyUC(userRepo, lobbyRepo, presenter);
        uc.execute(id, "TicTacToe");

        assertEquals(id, lobbyRepo.addedUser);
        assertEquals("TicTacToe", lobbyRepo.addedGame);
        assertEquals(id, presenter.lastId);
        assertEquals("Ищем игру в TicTacToe", presenter.lastMsg);
    }
}

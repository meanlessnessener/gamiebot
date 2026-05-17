package GamieBot.usecase;

import GamieBot.infra.repo.lobby.ILobbyRepo;
import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.domain.user.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class QuitLobbyUCTest {
    static class FakeUserRepo implements IUserRepo {
        private final UUID id;
        public FakeUserRepo(UUID id) { this.id = id; }
        @Override public User getUserByUUID(UUID id) { return new User(id, "n"); }
        @Override public void saveUser(UUID id, User user) {}
        @Override public UUID getUserByProvider(String provider, String token) { return null; }
        @Override public void saveUserByProvider(String provider, String token, UUID userId) {}
    }

    static class FakeLobbyRepo implements ILobbyRepo {
        public UUID removed;
        @Override public void addUserToLobby(UUID userId, String gameName) {}
        @Override public void removeUserFromLobby(UUID userId) { this.removed = userId; }
        @Override public java.util.List<UUID> getUsersInLobby(String gameName, int count) { return null; }
    }

    @Test
    public void removesUserFromLobby() {
        UUID id = UUID.randomUUID();
        FakeUserRepo userRepo = new FakeUserRepo(id);
        FakeLobbyRepo lobbyRepo = new FakeLobbyRepo();

        QuitLobbyUC uc = new QuitLobbyUC(userRepo, lobbyRepo);
        uc.execute(id);

        assertEquals(id, lobbyRepo.removed);
    }
}

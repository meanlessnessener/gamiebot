package GamieBot.adapter.controller.terminal;

import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.usecase.CommandRouter;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TerminalControllerTest {
    static class FakeUserRepo implements IUserRepo {
        private final UUID id;
        public FakeUserRepo(UUID id) { this.id = id; }
        @Override public GamieBot.domain.user.User getUserByUUID(UUID id) { return null; }
        @Override public void saveUser(UUID id, GamieBot.domain.user.User user) {}
        @Override public UUID getUserByProvider(String provider, String token) { return this.id; }
        @Override public void saveUserByProvider(String provider, String token, UUID userId) {}
    }

    static class CapturingRouter extends CommandRouter {
        public UUID lastUser;
        public String lastCommand;
        public String[] lastArgs;
        public CapturingRouter() { super(null); }
        @Override public void route(UUID userId, String command, String[] args) { this.lastUser = userId; this.lastCommand = command; this.lastArgs = args; }
    }

    @Test
    public void parsesTerminalMessageAndRoutes() {
        UUID existing = UUID.randomUUID();
        FakeUserRepo userRepo = new FakeUserRepo(existing);
        CapturingRouter router = new CapturingRouter();
        TerminalController controller = new TerminalController(userRepo, null, router);

        controller.onMessageReceived("alice", "/help please");

        assertEquals(existing, router.lastUser);
        assertEquals("/help", router.lastCommand);
        assertArrayEquals(new String[]{"please"}, router.lastArgs);
    }
}

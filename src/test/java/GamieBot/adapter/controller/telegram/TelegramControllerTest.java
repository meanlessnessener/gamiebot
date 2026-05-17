package GamieBot.adapter.controller.telegram;

import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.usecase.CommandRouter;
import org.junit.jupiter.api.Test;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TelegramControllerTest {
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
    public void parsesMessageAndRoutes() {
        UUID existing = UUID.randomUUID();
        FakeUserRepo userRepo = new FakeUserRepo(existing);
        CapturingRouter router = new CapturingRouter();
        TelegramController controller = new TelegramController(userRepo, null, router);

        Message msg = new Message();
        msg.setText("/play TicTacToe");
        Chat chat = new Chat();
        chat.setId(12345L);
        chat.setFirstName("Bob");
        msg.setChat(chat);

        controller.onUpdateReceived(msg);

        assertEquals(existing, router.lastUser);
        assertEquals("/play", router.lastCommand);
        assertNotNull(router.lastArgs);
        assertEquals(1, router.lastArgs.length);
        assertEquals("TicTacToe", router.lastArgs[0]);
    }
}

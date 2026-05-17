package GamieBot.adapter.presenter.terminal;

import GamieBot.infra.terminal.TerminalBot;
import GamieBot.infra.repo.user.IUserRepo;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TerminalPresenterTest {
    static class FakeTerminalBot extends TerminalBot {
        public String lastUser;
        public String lastMsg;
        @Override public boolean sendMessage(String userName, String message) { this.lastUser = userName; this.lastMsg = message; return true; }
    }

    static class FakeUserRepo implements IUserRepo {
        private final UUID id;
        private final String token;
        public FakeUserRepo(UUID id, String token) { this.id = id; this.token = token; }
        @Override public GamieBot.domain.user.User getUserByUUID(UUID id) { GamieBot.domain.user.User u = new GamieBot.domain.user.User(id, "n"); u.addProvider("terminal", token); return u; }
        @Override public void saveUser(UUID id, GamieBot.domain.user.User user) {}
        @Override public UUID getUserByProvider(String provider, String token) { return null; }
        @Override public void saveUserByProvider(String provider, String token, UUID userId) {}
    }

    @Test
    public void forwardsMessageToTerminalBot() {
        UUID id = UUID.randomUUID();
        FakeTerminalBot bot = new FakeTerminalBot();
        FakeUserRepo repo = new FakeUserRepo(id, "alice-chat");

        TerminalPresenter presenter = new TerminalPresenter(bot, repo);
        presenter.sendMessage(id, "hello");

        assertEquals("alice-chat", bot.lastUser);
        assertTrue(bot.lastMsg.contains("hello"));
    }
}

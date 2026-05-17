package GamieBot.adapter.presenter.telegram;

import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.infra.telegram.TelegramBot;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TelegramPresenterTest {
    static class FakeTelegramBot extends TelegramBot {
        public SendMessage last;
        @Override public void sendMessage(SendMessage message) { this.last = message; }
    }

    static class FakeUserRepo implements IUserRepo {
        private final UUID id;
        private final String token;
        public FakeUserRepo(UUID id, String token) { this.id = id; this.token = token; }
        @Override public GamieBot.domain.user.User getUserByUUID(UUID id) { GamieBot.domain.user.User u = new GamieBot.domain.user.User(id, "n"); u.addProvider("telegram", token); return u; }
        @Override public void saveUser(UUID id, GamieBot.domain.user.User user) {}
        @Override public UUID getUserByProvider(String provider, String token) { return null; }
        @Override public void saveUserByProvider(String provider, String token, UUID userId) {}
    }

    @Test
    public void buildsSendMessageAndCallsBot() {
        UUID id = UUID.randomUUID();
        FakeTelegramBot bot = new FakeTelegramBot();
        FakeUserRepo repo = new FakeUserRepo(id, "12345");

        TelegramPresenter presenter = new TelegramPresenter(bot, repo);
        presenter.sendMessage(id, "hi there");

        assertNotNull(bot.last);
        assertEquals("12345", bot.last.getChatId());
        assertEquals("`hi there`", bot.last.getText());
        assertEquals("MarkdownV2", bot.last.getParseMode());
    }
}

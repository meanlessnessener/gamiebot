package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;
import GamieBot.adapter.resources.TestMessageService;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class HelpUCTest {
    static class TestPresenter implements IPresenter {
        public UUID lastId;
        public String lastMsg;
        @Override
        public void sendMessage(UUID chatId, String msg) {
            this.lastId = chatId;
            this.lastMsg = msg;
        }
    }

    @Test
    public void sendsHelp() {
        TestPresenter presenter = new TestPresenter();
        HelpUC uc = new HelpUC(presenter, new TestMessageService());
        UUID id = UUID.randomUUID();
        uc.execute(id);

        assertEquals(id, presenter.lastId);
        assertEquals("Help message", presenter.lastMsg);
    }
}

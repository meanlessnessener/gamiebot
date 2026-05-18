package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;
import GamieBot.adapter.resources.TestMessageService;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class HelloUCTest {
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
    public void sendsHello() {
        TestPresenter presenter = new TestPresenter();
        HelloUC uc = new HelloUC(presenter, new TestMessageService());
        UUID id = UUID.randomUUID();
        uc.execute(id);

        assertEquals(id, presenter.lastId);
        assertEquals("Hello!", presenter.lastMsg);
    }
}

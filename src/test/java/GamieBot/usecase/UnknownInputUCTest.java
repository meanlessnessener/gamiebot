package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UnknownInputUCTest {
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
    public void sendsUnknownInputMessage() {
        TestPresenter presenter = new TestPresenter();
        UnknownInputUC uc = new UnknownInputUC(presenter);
        UUID id = UUID.randomUUID();
        uc.execute(id, "/foo bar");

        assertEquals(id, presenter.lastId);
        assertEquals("Неизвестная команда: /foo bar", presenter.lastMsg);
    }
}

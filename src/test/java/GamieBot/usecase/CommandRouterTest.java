package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CommandRouterTest {
    static class TestPresenter implements IPresenter {
        public UUID lastId;
        public String lastMsg;

        @Override
        public void sendMessage(UUID chatId, String msg) {
            this.lastId = chatId;
            this.lastMsg = msg;
        }
    }

    static class TestUCFactory extends UCFactory {
        private final IPresenter presenter;
        private final GamieBot.adapter.resources.TestMessageService messageService;

        public TestUCFactory(IPresenter presenter) {
            super(null, null, null, presenter, new GamieBot.adapter.resources.TestMessageService());
            this.presenter = presenter;
            this.messageService = new GamieBot.adapter.resources.TestMessageService();
        }

        @Override
        public HelloUC createHelloUC() { return new HelloUC(presenter, messageService); }

        @Override
        public HelpUC createHelpUC() { return new HelpUC(presenter, messageService); }

        @Override
        public JoinLobbyUC createJoinLobbyUC() { return new JoinLobbyUC(null, null, presenter, messageService); }

        @Override
        public TryMatchMakingUC createTryMatchMakingUC() { return new TryMatchMakingUC(null, null, null, presenter, messageService); }

        @Override
        public MakeMoveUC createMakeMoveUC() { return new MakeMoveUC(null, null, presenter, messageService); }

        @Override
        public QuitLobbyUC createQuitLobbyUC() { return new QuitLobbyUC(null, null, presenter, messageService); }

        @Override
        public QuitGameSessionUC createQuitGameSessionUC() { return new QuitGameSessionUC(null, null, presenter, messageService); }

        @Override
        public UnknownInputUC createUnknownInputUC() { return new UnknownInputUC(presenter); }
    }

    @Test
    public void testStartRoutesToHelloAndHelp() {
        TestPresenter presenter = new TestPresenter();
        TestUCFactory factory = new TestUCFactory(presenter);
        CommandRouter router = new CommandRouter(factory);

        UUID user = UUID.randomUUID();
        router.route(user, "/start", new String[]{});

        assertNotNull(presenter.lastMsg, "Presenter should receive a message");
        assertEquals(user, presenter.lastId, "Presenter should be called with given user id");
        // HelpUC is executed after HelloUC in /start, so last message should be help text
        assertEquals("Help message", presenter.lastMsg);
    }

    @Test
    public void testUnknownRoutesToUnknownInput() {
        TestPresenter presenter = new TestPresenter();
        TestUCFactory factory = new TestUCFactory(presenter);
        CommandRouter router = new CommandRouter(factory);

        UUID user = UUID.randomUUID();
        router.route(user, "/nope", new String[]{"arg1"});

        assertEquals(user, presenter.lastId);
        assertNotNull(presenter.lastMsg);
        assertTrue(presenter.lastMsg.contains("/nope"));
    }
}

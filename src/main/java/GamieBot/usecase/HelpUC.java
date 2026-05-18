package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;
import GamieBot.adapter.resources.IMessageService;
import java.util.UUID;

public class HelpUC {
    private final IPresenter presenter;
    private final IMessageService messageService;

    public HelpUC(IPresenter presenter, IMessageService messageService) {
        this.presenter = presenter;
        this.messageService = messageService;
    }

    public void execute(UUID userId) {
        String text = messageService.get("help", null);
        presenter.sendMessage(userId, text);
    }
}
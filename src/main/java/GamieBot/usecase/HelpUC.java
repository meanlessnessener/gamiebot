package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;
import java.util.UUID;

public class HelpUC {
    private final IPresenter presenter;

    public HelpUC(IPresenter presenter) {
        this.presenter = presenter;
    }

    public void execute(UUID userId) {
        String text = "Help message";
        presenter.sendMessage(userId, text);
    }
}
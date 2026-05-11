package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;

import java.util.UUID;

public class HelloUC {
    private final IPresenter presenter;

    public HelloUC(IPresenter presenter) {
        this.presenter = presenter;
    }

    public void execute(UUID userId) {
        String text = "Hello!";
        presenter.sendMessage(userId, text);
    }
}

package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;
import java.util.UUID;

public class UnknownInputUC {
    private final IPresenter presenter;

    public UnknownInputUC(IPresenter presenter) {
        this.presenter = presenter;
    }

    public void execute(UUID userId, String input) {
        presenter.sendMessage(userId, "Неизвестная команда: " + input);
    }
}
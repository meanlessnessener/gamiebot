package GamieBot;

import GamieBot.presenter.Presenter;
import GamieBot.view.IView;
import GamieBot.view.TelegramBotView;


public class Main {
    public static void main(String[] args) throws Exception {
        IView telegramBotView = new TelegramBotView();

        Presenter presenter = new Presenter(telegramBotView);

        presenter.run();
    }
}

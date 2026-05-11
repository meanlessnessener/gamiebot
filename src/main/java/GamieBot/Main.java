package GamieBot;

import GamieBot.infra.repo.lobby.ILobbyRepo;
import GamieBot.infra.repo.lobby.InMemoryLobbyRepo;
import GamieBot.infra.repo.session.IGameSessionRepo;
import GamieBot.infra.repo.session.InMemoryGameSessionRepo;
import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.infra.repo.user.InMemoryUserRepo;
import GamieBot.infra.telegram.TelegramBot;

import GamieBot.adapter.controller.telegram.ITelegramController;
import GamieBot.adapter.controller.telegram.TelegramController;
import GamieBot.adapter.presenter.IPresenter;
import GamieBot.adapter.presenter.telegram.TelegramPresenter;
import GamieBot.usecase.UCFactory;

public class Main {
    public static void main(String[] args) {
        TelegramBot telegramBot = new TelegramBot();
        IUserRepo userRepo = new InMemoryUserRepo();
        ILobbyRepo lobbyRepo = new InMemoryLobbyRepo();
        IGameSessionRepo gameSessionRepo = new InMemoryGameSessionRepo();
        
        IPresenter presenter = new TelegramPresenter(telegramBot, userRepo);
        UCFactory ucFactory = new UCFactory(userRepo, lobbyRepo, gameSessionRepo, presenter);
        ITelegramController controller = new TelegramController(userRepo, lobbyRepo, ucFactory);
        
        telegramBot.setController(controller);
        telegramBot.run();
    }
}

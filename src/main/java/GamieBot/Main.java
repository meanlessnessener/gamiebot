package GamieBot;

import GamieBot.infra.repo.lobby.ILobbyRepo;
import GamieBot.infra.repo.lobby.InMemoryLobbyRepo;
import GamieBot.infra.repo.session.IGameSessionRepo;
import GamieBot.infra.repo.session.InMemoryGameSessionRepo;
import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.infra.repo.user.InMemoryUserRepo;
import GamieBot.infra.telegram.TelegramBot;
import GamieBot.infra.terminal.TerminalBot;

import java.io.IOException;

import GamieBot.adapter.controller.telegram.ITelegramController;
import GamieBot.adapter.controller.telegram.TelegramController;
import GamieBot.adapter.controller.terminal.ITerminalController;
import GamieBot.adapter.controller.terminal.TerminalController;
import GamieBot.adapter.presenter.IPresenter;
import GamieBot.adapter.presenter.telegram.TelegramPresenter;
import GamieBot.adapter.presenter.terminal.TerminalPresenter;

import GamieBot.usecase.CommandRouter;
import GamieBot.usecase.UCFactory;

public class Main {
    public static void main(String[] args) {
        TelegramBot telegramBot = new TelegramBot();
        TerminalBot terminalBot = new TerminalBot();
        
        IUserRepo userRepo = new InMemoryUserRepo();
        ILobbyRepo lobbyRepo = new InMemoryLobbyRepo();
        IGameSessionRepo gameSessionRepo = new InMemoryGameSessionRepo();
        
        IPresenter telegramPresenter = new TelegramPresenter(telegramBot, userRepo);
        IPresenter terminalPresenter = new TerminalPresenter(terminalBot, userRepo);
        
        // Keep UCFactory wired to the Telegram presenter (existing behavior).
        UCFactory ucFactory = new UCFactory(userRepo, lobbyRepo, gameSessionRepo, terminalPresenter);
        CommandRouter commandRouter = new CommandRouter(ucFactory);
        
        ITelegramController telegramController = new TelegramController(userRepo, ucFactory, commandRouter);
        ITerminalController terminalController = new TerminalController(userRepo, ucFactory, commandRouter);
        
        telegramBot.setController(telegramController);
        terminalBot.setController(terminalController);

        // start the terminal socket server (non-blocking)
        try {
            terminalBot.startUnixSocketServer();
        } catch (Exception e) {
            System.err.println("Failed to start TerminalBot socket server: " + e.getMessage());
        }

        telegramBot.run();
    }
}

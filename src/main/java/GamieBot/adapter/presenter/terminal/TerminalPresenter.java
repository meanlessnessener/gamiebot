package GamieBot.adapter.presenter.terminal;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import GamieBot.adapter.presenter.IPresenter;
import GamieBot.domain.user.User;
import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.infra.terminal.TerminalBot;

public class TerminalPresenter implements IPresenter {
    private static final Logger log = LoggerFactory.getLogger(TerminalPresenter.class);
    private final TerminalBot terminalBot;
    private final IUserRepo userRepo;

    public TerminalPresenter(TerminalBot terminalBot, IUserRepo userRepo) {
        this.terminalBot = terminalBot;
        this.userRepo = userRepo;
    }

    @Override
    public void sendMessage(UUID userId, String message) {
        User user = userRepo.getUserByUUID(userId);
        String chatId = user.getProviderToken("terminal");
        terminalBot.sendMessage(chatId, message);
        log.info("Sent message to user {}: {}", chatId, message);
    }
}
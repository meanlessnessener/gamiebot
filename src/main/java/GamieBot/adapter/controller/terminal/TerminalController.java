package GamieBot.adapter.controller.terminal;

import java.util.Arrays;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.usecase.CommandRouter;
import GamieBot.usecase.RegisterNewUserUC;
import GamieBot.usecase.UCFactory;

public class TerminalController implements ITerminalController {
    private static final Logger log = LoggerFactory.getLogger(TerminalController.class);
    private final IUserRepo userRepo;
    private final UCFactory ucFactory;
    private final CommandRouter commandRouter;

    public TerminalController(IUserRepo userRepo, UCFactory ucFactory, CommandRouter commandRouter) {
        this.userRepo = userRepo;
        this.ucFactory = ucFactory;
        this.commandRouter = commandRouter;
    }

    @Override
    public void onMessageReceived(String chatId, String message) {
        String[] parts = message.split(" ");
        String command = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        log.info("Message received from user {}: {}, {}", chatId, command, String.join(" ", args));

        String name = chatId;
        UUID userId = getOrRegisterUser(chatId, name);

        commandRouter.route(userId, command, args);
    }

    private UUID getOrRegisterUser(String chatId, String name) {
        UUID userId = userRepo.getUserByProvider("terminal", chatId);
        if (userId == null) {
            log.info("Registering new terminal user {}", chatId);
            RegisterNewUserUC registerNewUserUC = ucFactory.createRegisterNewUserUC();
            userId = registerNewUserUC.execute("terminal", chatId, name);
        }
        return userId;
    }
}

package GamieBot.adapter.controller.telegram;

import java.util.UUID;

import org.telegram.telegrambots.meta.api.objects.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.usecase.CommandRouter;

import GamieBot.usecase.RegisterNewUserUC;
import GamieBot.usecase.UCFactory;

public class TelegramController implements ITelegramController {
    private static final Logger log = LoggerFactory.getLogger(TelegramController.class);
    private final IUserRepo userRepo;
    private final UCFactory ucFactory;
    private final CommandRouter commandRouter;

    public TelegramController(IUserRepo userRepo, UCFactory ucFactory, CommandRouter commandRouter) {
        log.info("TelegramController initialized");
        this.userRepo = userRepo;
        this.ucFactory = ucFactory;
        this.commandRouter = commandRouter;
    }

    public void onUpdateReceived(Message message) {
        String chatId = message.getChatId().toString();
        String[] parts = message.getText().split(" ", 2);
        String command = parts[0];
        String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];
        
        log.info("Message received from chatId {}: {}, {}", chatId, command, String.join(" ", args));

        String name = message.getChat().getFirstName() != null ? message.getChat().getFirstName() : "Unknown";
        UUID userId = getOrRegisterUser(chatId, name);

        commandRouter.route(userId, command, args);
    }

    private UUID getOrRegisterUser(String chatId, String name) {
        UUID userId = userRepo.getUserByProvider("telegram", chatId);
        if (userId == null) {
            log.info("Registering new telegram user for chatId {}", chatId);
            RegisterNewUserUC registerNewUserUC = ucFactory.createRegisterNewUserUC();
            userId = registerNewUserUC.execute("telegram", chatId, name);
        }
        return userId;
    }
}

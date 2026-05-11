package GamieBot.adapter.controller.telegram;

import java.util.Arrays;
import java.util.UUID;

import org.telegram.telegrambots.meta.api.objects.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import GamieBot.infra.repo.lobby.ILobbyRepo;
import GamieBot.infra.repo.user.IUserRepo;

import GamieBot.usecase.*;

public class TelegramController implements ITelegramController {
    private static final Logger log = LoggerFactory.getLogger(TelegramController.class);
    private final IUserRepo userRepo;
    private final ILobbyRepo lobbyRepo;
    private final UCFactory ucFactory;

    public TelegramController(IUserRepo userRepo, ILobbyRepo lobbyRepo, UCFactory ucFactory) {
        log.info("TelegramController initialized");
        this.userRepo = userRepo;
        this.lobbyRepo = lobbyRepo;
        this.ucFactory = ucFactory;
    }

    public void onUpdateReceived(Message message) {
        String chatId = message.getChatId().toString();
        String[] parts = message.getText().split(" ");
        String command = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        log.info("Message received from chatId {}: {}, {}", chatId, command, String.join(" ", args));

        UUID userId = getOrRegisterUser(chatId);

        route(userId, command, args);
    }

    private UUID getOrRegisterUser(String chatId) {
        UUID userId = userRepo.getUserByProvider("telegram", chatId);
        if (userId == null) {
            log.info("Registering new telegram user for chatId {}", chatId);
            RegisterNewUserUC registerNewUserUC = ucFactory.createRegisterNewUserUC();
            userId = registerNewUserUC.execute("telegram", chatId);
        }
        return userId;
    }

    private void route(UUID userId, String command, String[] args) {
        switch (command) {
            case "/start":
                log.info("Routing to start command for userId {}", userId);
                handleStartCommand(userId);
                break;
            case "/help":
                log.info("Routing to help command for userId {}", userId);
                handleHelpCommand(userId);
                break;
            case "/play":
                log.info("Routing to play command for userId {} with args: {}", userId, String.join(" ", args));
                handlePlayCommand(userId, args);
                break;
            case "/move":
                log.info("Routing to move command for userId {} with args: {}", userId, String.join(" ", args));                break;
            case "/quitLobby":
                log.info("Routing to quitLobby command for userId {}", userId);
                break;
            case "/quitGame":
                log.info("Routing to quitGame command for userId {}", userId);
                break;
            default:
                log.warn("Unknown command received from userId {}: {}", userId, command);
                break;
        }
    }

    private void handleStartCommand(UUID userId) {
        log.info("Handling start command for userId {}", userId);
        HelloUC helloUC = ucFactory.createHelloUC();
        helloUC.execute(userId);
        HelpUC helpUC = ucFactory.createHelpUC();
        helpUC.execute(userId);
    }

    private void handleHelpCommand(UUID userId) {
        log.info("Handling help command for userId {}", userId);
        HelpUC helpUC = ucFactory.createHelpUC();
        helpUC.execute(userId);
    }

    private void handlePlayCommand(UUID userId, String[] args) {
        log.info("Handling play command for userId {} with args: {}", userId, String.join(" ", args));
        if (args.length == 0) {
            log.warn("No game specified in play command for userId {}", userId);
            return;
        }
        JoinLobbyUC joinLobbyUC = ucFactory.createJoinLobbyUC();
        joinLobbyUC.execute(userId, args[0]);
        TryMatchMakingUC tryMatchMakingUC = ucFactory.createTryMatchMakingUC();
        tryMatchMakingUC.execute(args[0]);
    }
}

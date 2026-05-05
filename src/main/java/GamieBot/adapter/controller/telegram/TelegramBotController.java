package GamieBot.adapter.controller;

import java.util.Arrays;
import java.util.UUID;

import org.telegram.telegrambots.meta.api.objects.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import GamieBot.adapter.controller.telegram.ITelegramController;
import GamieBot.adapter.controller.telegram.ITelegramUserMappingRepo;

public class TelegramBotController implements ITelegramController {
    private static final Logger log = LoggerFactory.getLogger(TelegramBotController.class);
    private final ITelegramUserMappingRepo userMappingRepo;

    public TelegramBotController(ITelegramUserMappingRepo userMappingRepo) {
        this.userMappingRepo = userMappingRepo;
        log.info("TelegramBotController initialized");
    }

    @Override
    public void onUpdateReceived(Message message) {
        String chatId = message.getChatId().toString();
        String[] parts = message.getText().split(" ");
        String command = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        log.info("Message received from chatId {}: {}, {}", chatId, command, String.join(" ", args));

        UUID userId = getUserIdByChatId(chatId);

        route(userId, command, args);
    }

    private UUID getUserIdByChatId(String chatId) {
        if (userMappingRepo.getUserIdByChatId(chatId) == null) {
            UUID newUserId = UUID.randomUUID();
            userMappingRepo.addMapping(newUserId, chatId);
            log.info("New user mapping created: {} -> {}", newUserId, chatId);
        }
        return userMappingRepo.getUserIdByChatId(chatId);
    }

    private void route(UUID userId, String command, String[] args) {
        switch (command) {
            case "/start":
                log.info("Routing to start command for userId {}", userId);
                break;
            case "/help":
                log.info("Routing to help command for userId {}", userId);
                break;
            case "/play":
                log.info("Routing to play command for userId {} with args: {}", userId, String.join(" ", args));
                break;
            case "/move":
                log.info("Routing to move command for userId {} with args: {}", userId, String.join(" ", args));
                break;
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
}

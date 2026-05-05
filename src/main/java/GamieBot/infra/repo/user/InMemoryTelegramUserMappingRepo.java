package GamieBot.infra.telegram;

import java.util.HashMap;
import java.util.UUID;

import GamieBot.adapter.controller.telegram.ITelegramUserMappingRepo;

public class InMemoryTelegramUserMappingRepo implements ITelegramUserMappingRepo {
    private final HashMap<UUID, String> userIdToChatId = new HashMap<>();
    private final HashMap<String, UUID> chatIdToUserId = new HashMap<>();

    public UUID getUserIdByChatId(String chatId) {
        return chatIdToUserId.get(chatId);
    }

    public String getChatIdByUserId(UUID userId) {
        return userIdToChatId.get(userId);
    }

    public void addMapping(UUID userId, String chatId) {
        userIdToChatId.put(userId, chatId);
        chatIdToUserId.put(chatId, userId);
    }
    
}

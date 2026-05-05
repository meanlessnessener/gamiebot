package GamieBot.infra.repo.user;

public interface ITelegramUserMappingRepo {
    public String getUserIdByChatId(String chatId);
    public String getChatIdByUserId(String userId);
    public void addMapping(String userId, String chatId);
}
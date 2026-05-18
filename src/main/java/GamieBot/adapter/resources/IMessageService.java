package GamieBot.adapter.resources;

public interface IMessageService {
    String get(String key, String lang, Object... args);
}
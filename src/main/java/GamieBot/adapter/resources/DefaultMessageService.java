package GamieBot.adapter.resources;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.HashMap;

public class DefaultMessageService implements IMessageService {
    private final HashMap<String, ResourceBundle> bundles = new HashMap<>();

    public DefaultMessageService() {
        bundles.put("default", ResourceBundle.getBundle("messages", Locale.forLanguageTag("ru-RU")));
        bundles.put("ru", ResourceBundle.getBundle("messages", Locale.forLanguageTag("ru-RU")));
        bundles.put("en", ResourceBundle.getBundle("messages", Locale.forLanguageTag("en-US")));
    }
    
    public String get(String key, String lang, Object... args) {
        ResourceBundle bundle = bundles.getOrDefault(lang, bundles.get("default"));
        String pattern = bundle.getString(key);
        return MessageFormat.format(pattern, args);
    }
}
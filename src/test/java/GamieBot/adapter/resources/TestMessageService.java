package GamieBot.adapter.resources;

public class TestMessageService implements IMessageService {
    @Override
    public String get(String key, String lang, Object... args) {
        switch (key) {
            case "hello":
                return "Hello!";
            case "help":
                return "Help message";
            case "joinLobby.searchingGame":
                return String.format("Searching game %s", args);
            case "joinLobby.gameNameRequired":
                return "Game name required";
            case "joinLobby.gameDoesNotExist":
                return String.format("Game does not exist: %s", args);
            case "matchMaking.gameStarted":
                return "Game started!";
            case "matchMaking.yourMove":
                return "Your move";
            case "matchMaking.othersMove":
                return "Others move";
            default:
                if (args != null && args.length > 0) {
                    // support simple formatting
                    Object[] fmtArgs = args;
                    try {
                        return String.format(key, fmtArgs);
                    } catch (Exception e) {
                        // fallback
                        StringBuilder sb = new StringBuilder(key);
                        for (Object a : args) sb.append(" ").append(a);
                        return sb.toString();
                    }
                }
                return key;
        }
    }
}

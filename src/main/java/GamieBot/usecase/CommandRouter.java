package GamieBot.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class CommandRouter {
    private static final Logger log = LoggerFactory.getLogger(CommandRouter.class);
    private final UCFactory ucFactory;

    public CommandRouter(UCFactory ucFactory) {
        this.ucFactory = ucFactory;
    }

    public void route(UUID userId, String command, String[] args) {
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
                log.info("Routing to move command for userId {} with args: {}", userId, String.join(" ", args));
                handleMoveCommand(userId, args);
                break;
            case "/quitLobby":
                log.info("Routing to quitLobby command for userId {}", userId);
                handleQuitLobbyCommand(userId);
                break;
            case "/quitGame":
                log.info("Routing to quitGame command for userId {}", userId);
                handleQuitGameCommand(userId);
                break;
            default:
                log.warn("Unknown command received from userId {}: {}", userId, command);
                handleUnknownCommand(userId, command + String.join(" ", args));
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
        String gameName = args.length > 0 ? args[0] : null;
        
        JoinLobbyUC joinLobbyUC = ucFactory.createJoinLobbyUC();
        joinLobbyUC.execute(userId, gameName);
        TryMatchMakingUC tryMatchMakingUC = ucFactory.createTryMatchMakingUC();
        tryMatchMakingUC.execute(gameName);
    }
    
    private void handleMoveCommand(UUID userId, String[] args) {
        log.info("Handling move command for userId {} with args: {}", userId, String.join(" ", args));
        String move = args.length > 0 ? String.join(" ", args) : null;
        
        MakeMoveUC makeMoveUC = ucFactory.createMakeMoveUC();
        makeMoveUC.execute(userId, move);
    }
    
    private void handleQuitLobbyCommand(UUID userId) {
        log.info("Handling quitLobby command for userId {}", userId);
        QuitLobbyUC quitLobbyUC = ucFactory.createQuitLobbyUC();
        quitLobbyUC.execute(userId);
    }
    
    private void handleQuitGameCommand(UUID userId) {
        log.info("Handling quitGame command for userId {}", userId);
        QuitGameSessionUC quitGameSessionUC = ucFactory.createQuitGameSessionUC();
        quitGameSessionUC.execute(userId);
    }
    
    private void handleUnknownCommand(UUID userId, String input) {
        log.info("Handling unknown command for userId {}", userId);
        UnknownInputUC unknownInputUC = ucFactory.createUnknownInputUC();
        unknownInputUC.execute(userId, input);
    }
}
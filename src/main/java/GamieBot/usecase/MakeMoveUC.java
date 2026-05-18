package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;
import GamieBot.adapter.resources.IMessageService;
import GamieBot.domain.gameSession.GameSession;
import GamieBot.exception.gameSession.AnotherPlayersTurnException;
import GamieBot.exception.gameSession.GameIsAlreadyFinishedException;
import GamieBot.exception.gameSession.InvalidMoveException;
import GamieBot.exception.gameSession.PlayerNotFoundException;
import GamieBot.infra.repo.session.IGameSessionRepo;
import java.util.UUID;

public class MakeMoveUC {

    private final IGameSessionRepo gameSessionRepo;
    private final IPresenter presenter;
    private final IMessageService messageService;

    public MakeMoveUC(
        IGameSessionRepo gameSessionRepo,
        IPresenter presenter,
        IMessageService messageService
    ) {
        this.gameSessionRepo = gameSessionRepo;
        this.presenter = presenter;
        this.messageService = messageService;
    }

    public void execute(UUID userId, String move) {
        GameSession session = gameSessionRepo.getSessionByUserUUID(userId);
        if (session == null) {
            return;
        }
        try {
            session.makeMove(userId, move);
        } catch (PlayerNotFoundException e) {
            String text = messageService.get("makeMove.playerNotFound", null);
            presenter.sendMessage(userId, text);
        } catch (GameIsAlreadyFinishedException e) {
            String text = messageService.get(
                "makeMove.gameIsAlreadyFinished",
                null
            );
            presenter.sendMessage(userId, text);
        } catch (AnotherPlayersTurnException e) {
            String text = messageService.get(
                "makeMove.anotherPlayersTurn",
                null
            );
            presenter.sendMessage(userId, text);
        } catch (InvalidMoveException e) {
            String text = messageService.get(
                "makeMove.invalidMoveException",
                null
            );
            presenter.sendMessage(userId, text);
        }

        gameSessionRepo.saveSession(session);

        for (UUID playerId : session.getPlayers()) {
            String gameState = session.getGameStateForPlayer(playerId);
            presenter.sendMessage(playerId, gameState);
        }
    }
}

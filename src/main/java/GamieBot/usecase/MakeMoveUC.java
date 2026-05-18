package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;
import GamieBot.adapter.resources.IMessageService;
import GamieBot.domain.gameSession.GameSession;
import GamieBot.exception.gameSession.AnotherPlayersTurnException;
import GamieBot.exception.gameSession.GameIsAlreadyFinishedException;
import GamieBot.exception.gameSession.InvalidMoveException;
import GamieBot.exception.gameSession.PlayerNotFoundException;
import GamieBot.infra.repo.session.IGameSessionRepo;
import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.domain.user.User;
import GamieBot.domain.user.UserStatus;

import java.util.UUID;

public class MakeMoveUC {

    private final IUserRepo userRepo;
    private final IGameSessionRepo gameSessionRepo;
    private final IPresenter presenter;
    private final IMessageService messageService;

    public MakeMoveUC(
        IUserRepo userRepo,
        IGameSessionRepo gameSessionRepo,
        IPresenter presenter,
        IMessageService messageService
    ) {
        this.userRepo = userRepo;
        this.gameSessionRepo = gameSessionRepo;
        this.presenter = presenter;
        this.messageService = messageService;
    }

    public void execute(UUID userId, String move) {
        GameSession session = gameSessionRepo.getSessionByUserUUID(userId);
        User user = userRepo.getUserByUUID(userId);
        if (user == null) {
            return;
        }
        if (session == null || session.isFinished() || user.getStatus() != UserStatus.INGAME) {
            String text = messageService.get("makeMove.notInGame", null);
            presenter.sendMessage(userId, text);
            return;
        }
        
        boolean isMoveMade = false;
        try {
            session.makeMove(userId, move);
            isMoveMade = true;
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

        if (!isMoveMade) {
            return;
        }

        for (UUID playerId : session.getPlayers()) {
            String gameState = session.getGameStateForPlayer(playerId);
            presenter.sendMessage(playerId, gameState);

            if (session.isFinished()) {
                String text = messageService.get("makeMove.gameIsFinished", null);
                presenter.sendMessage(playerId, text);

                if (userId.equals(session.getWinner())) {
                    String text1 = messageService.get("makeMove.youAreWinner", null);
                    presenter.sendMessage(playerId, text1);
                } else {
                    String text1 = messageService.get("makeMove.youAreLoser", null);
                    presenter.sendMessage(playerId, text1);
                }

                User player = userRepo.getUserByUUID(playerId);
                player.setStatus(UserStatus.IDLE);
                userRepo.saveUser(playerId, user);
                
                continue;
            }
            
            if (playerId.equals(userId)) {
                String text1 = messageService.get("makeMove.yourMove", null);
                presenter.sendMessage(playerId, text1);
            } else {
                String text1 = messageService.get("makeMove.othersMove", null);
                presenter.sendMessage(playerId, text1);
            }
        }
    }
}

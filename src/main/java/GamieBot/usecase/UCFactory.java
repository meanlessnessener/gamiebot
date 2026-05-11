package GamieBot.usecase;

import GamieBot.infra.repo.lobby.ILobbyRepo;
import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.infra.repo.session.IGameSessionRepo;

import GamieBot.adapter.presenter.IPresenter;

public class UCFactory {
    private final IUserRepo userRepo;
    private final ILobbyRepo lobbyRepo;
    private final IGameSessionRepo gameSessionRepo;
    private final IPresenter presenter;

    public UCFactory(IUserRepo userRepo, ILobbyRepo lobbyRepo, IGameSessionRepo gameSessionRepo, IPresenter presenter) {
        this.userRepo = userRepo;
        this.lobbyRepo = lobbyRepo;
        this.gameSessionRepo = gameSessionRepo;
        this.presenter = presenter;
    }

    public HelloUC createHelloUC() {
        return new HelloUC(presenter);
    }

    public HelpUC createHelpUC() {
        return new HelpUC(presenter);
    }

    public RegisterNewUserUC createRegisterNewUserUC() {
        return new RegisterNewUserUC(userRepo);
    }

    public JoinLobbyUC createJoinLobbyUC() {
        return new JoinLobbyUC(userRepo, lobbyRepo, presenter);
    }

    public TryMatchMakingUC createTryMatchMakingUC() {
        return new TryMatchMakingUC(userRepo, lobbyRepo, gameSessionRepo, presenter);
    }

    public MakeMoveUC createMakeMoveUC() {
        return new MakeMoveUC(gameSessionRepo, presenter);
    }

    public QuitLobbyUC createQuitLobbyUC() {
        return new QuitLobbyUC(userRepo, lobbyRepo);
    }

    public QuitGameSessionUC createQuitGameSessionUC() {
        return new QuitGameSessionUC(userRepo, gameSessionRepo, presenter);
    }

    public UnknownInputUC createUnknownInputUC() {
        return new UnknownInputUC(presenter);
    }
}
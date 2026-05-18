package GamieBot.usecase;

import GamieBot.infra.repo.lobby.ILobbyRepo;
import GamieBot.infra.repo.user.IUserRepo;
import GamieBot.infra.repo.session.IGameSessionRepo;
import GamieBot.adapter.presenter.IPresenter;
import GamieBot.adapter.resources.IMessageService;

public class UCFactory {
    private final IUserRepo userRepo;
    private final ILobbyRepo lobbyRepo;
    private final IGameSessionRepo gameSessionRepo;
    private final IPresenter presenter;
    private final IMessageService messageService;

    public UCFactory(IUserRepo userRepo, ILobbyRepo lobbyRepo, IGameSessionRepo gameSessionRepo, IPresenter presenter, IMessageService messageService) {
        this.userRepo = userRepo;
        this.lobbyRepo = lobbyRepo;
        this.gameSessionRepo = gameSessionRepo;
        this.presenter = presenter;
        this.messageService = messageService;
    }

    public HelloUC createHelloUC() {
        return new HelloUC(presenter, messageService);
    }

    public HelpUC createHelpUC() {
        return new HelpUC(presenter, messageService);
    }

    public RegisterNewUserUC createRegisterNewUserUC() {
        return new RegisterNewUserUC(userRepo);
    }

    public JoinLobbyUC createJoinLobbyUC() {
        return new JoinLobbyUC(userRepo, lobbyRepo, presenter, messageService);
    }

    public TryMatchMakingUC createTryMatchMakingUC() {
        return new TryMatchMakingUC(userRepo, lobbyRepo, gameSessionRepo, presenter, messageService);
    }

    public MakeMoveUC createMakeMoveUC() {
        return new MakeMoveUC(userRepo, gameSessionRepo, presenter, messageService);
    }

    public QuitLobbyUC createQuitLobbyUC() {
        return new QuitLobbyUC(userRepo, lobbyRepo, presenter, messageService);
    }

    public QuitGameSessionUC createQuitGameSessionUC() {
        return new QuitGameSessionUC(userRepo, gameSessionRepo, presenter, messageService);
    }

    public UnknownInputUC createUnknownInputUC() {
        return new UnknownInputUC(presenter);
    }
}

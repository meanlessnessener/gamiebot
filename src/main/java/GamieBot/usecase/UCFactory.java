package GamieBot.usecase;

import GamieBot.adapter.presenter.IPresenter;

import GamieBot.infra.repo.lobby.ILobbyRepo;
import GamieBot.infra.repo.user.IUserRepo;

public class UCFactory {
    private final IUserRepo userRepo;
    private final ILobbyRepo lobbyRepo;
    private final IPresenter presenter;

    public UCFactory(IUserRepo userRepo, ILobbyRepo lobbyRepo, IPresenter presenter) {
        this.userRepo = userRepo;
        this.lobbyRepo = lobbyRepo;
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
}
package GamieBot.usecase;

import java.util.UUID;

import GamieBot.domain.user.User;
import GamieBot.infra.repo.user.IUserRepo;

public class RegisterNewUserUC {
    private final IUserRepo userRepo;

    public RegisterNewUserUC(IUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public UUID execute(String provider, String token, String name) {
        UUID id = UUID.randomUUID();
        User user = new User(id, name);
        user.addProvider(provider, token);
        
        userRepo.saveUser(id, user);
        userRepo.saveUserByProvider(provider, token, id);
        
        return id;
    }
}
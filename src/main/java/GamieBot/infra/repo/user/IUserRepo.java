package GamieBot.infra.repo.user;

import java.util.UUID;

import GamieBot.domain.user.User;

public interface IUserRepo {
    public User getUserByUUID(UUID id);
    public void saveUser(UUID id, User user);
    public UUID getUserByProvider(String provider, String token);
    public void saveUserByProvider(String provider, String token, UUID userId);
}
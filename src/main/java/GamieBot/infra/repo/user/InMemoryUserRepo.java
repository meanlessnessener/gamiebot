package GamieBot.infra.repo.user;

import java.util.HashMap;
import java.util.UUID;

import GamieBot.domain.user.User;
import GamieBot.domain.user.UserProvider;

public class InMemoryUserRepo implements IUserRepo {
    private final HashMap<UUID, User> repo = new HashMap<>();
    private final HashMap<UserProvider, UUID> providerMap = new HashMap<>();

    public User getUserByUUID(UUID id) {
        return repo.get(id);
    }

    public void saveUser(UUID id, User user) {
        repo.put(id, user);
    }

    public UUID getUserByProvider(String provider, String token) {
        return providerMap.get(new UserProvider(provider, token));
    }

    public void saveUserByProvider(String provider, String token, UUID userId) {
        providerMap.put(new UserProvider(provider, token), userId);
    }
}

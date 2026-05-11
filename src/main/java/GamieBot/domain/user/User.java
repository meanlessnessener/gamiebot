package GamieBot.domain.user;

import java.util.ArrayList;
import java.util.UUID;

public class User {
    private final UUID uuid;
    private UserStatus status;
    private ArrayList<UserProvider> providers;

    public User(UUID uuid) {
        this.uuid = uuid;
        this.status = UserStatus.IDLE;
        this.providers = new ArrayList<>();
    }

    public void addProvider(String provider, String token) {
        this.providers.add(UserProvider.of(provider, token));
    }

    public void removeProvider(String provider) {
        this.providers.removeIf(p -> p.getProvider().equals(provider));
    }

    public ArrayList<UserProvider> getProviders() {
        return this.providers;
    }

    public String getProviderToken(String provider) {
        return this.providers.stream()
            .filter(p -> p.getProvider().equals(provider))
            .map(UserProvider::getToken)
            .findFirst()
            .orElse(null);
    }

    public UserStatus getStatus() {
        return this.status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }    
}

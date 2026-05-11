package GamieBot.domain.user;

public record UserProvider(
    String provider,
    String token
) {
    public static UserProvider of(String provider, String token) {
        return new UserProvider(provider, token);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProvider that = (UserProvider) o;
        return provider.equals(that.provider) && token.equals(that.token);
    }

    @Override
    public int hashCode() {
        return provider.hashCode() * 31 + token.hashCode();
    }

    public String getProvider() {
        return provider;
    }

    public String getToken() {
        return token;
    }    
}
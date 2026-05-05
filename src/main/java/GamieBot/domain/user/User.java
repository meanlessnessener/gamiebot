package GamieBot.model.users;

import java.util.UUID;

public class User {
    public UUID uuid;
    public UserStatus status;

    User(UUID uuid) {
        this.uuid = uuid;
        this.status = UserStatus.IDLE;
    }
}

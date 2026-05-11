package GamieBot.domain.lobby;

import java.util.List;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;


public class Lobby {
    private final String gameName;
    private final Queue<UUID> queue;
    private int size = 0;

    public Lobby(String gameName, Queue<UUID> queue) {
        this.gameName = gameName;
        this.queue = queue;
    }

    public Lobby(String gameName) {
        this.gameName = gameName;
        this.queue = new ArrayDeque<UUID>(); // todo: find queue implementation
    }

    public void addUser(UUID id) {
        queue.add(id);
        size++;
    }

    private UUID extractUser() {
        UUID id = queue.remove();
        size--;
        return id;
    }

    public int getSize() {
        return size;
    }

    public String getGameName() {
        return gameName;
    }

    public List<UUID> extractUsers(int count) throws Exception {
        if (size < count) {
            throw new Exception("not enough users to extract");
        }
        List<UUID> result = new ArrayList<UUID>();
        for (int i = 0; i < count; i++) {
            result.add(extractUser());
        }
        return result;
    }
}
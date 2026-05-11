package GamieBot.infra.repo.lobby;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

public class InMemoryLobbyRepo implements ILobbyRepo {
    private final HashMap<String, LinkedHashSet<UUID>> lobbies = new HashMap<>();
    private final HashMap<UUID, String> lobbyOfUser = new HashMap<>();

    @Override
    public void addUserToLobby(UUID userId, String gameName) {
        lobbies.computeIfAbsent(gameName, k -> new LinkedHashSet<>()).add(userId);
        lobbyOfUser.put(userId, gameName);
    }

    @Override
    public void removeUserFromLobby(UUID userId) {
        String gameName = lobbyOfUser.get(userId);
        if (gameName != null) {
            lobbies.get(gameName).remove(userId);
            lobbyOfUser.remove(userId);
        }
    }

    @Override
    public List<UUID> getUsersInLobby(String gameName, int count) {
        if (lobbies.get(gameName) == null) {
            return null;
        }
        if (lobbies.get(gameName).size() < count) {
            return null;
        }
        List<UUID> response = lobbies.get(gameName).stream().limit(count).toList();
        for (UUID userId : response) {
            lobbyOfUser.remove(userId);
            lobbies.get(gameName).remove(userId);
        }
        return response;
    }
}
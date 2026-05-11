package GamieBot.infra.repo.lobby;

import java.util.List;
import java.util.UUID;

public interface ILobbyRepo {
	void addUserToLobby(UUID userId, String gameName);
	void removeUserFromLobby(UUID userId);
	List<UUID> getUsersInLobby(String gameName, int count);
}
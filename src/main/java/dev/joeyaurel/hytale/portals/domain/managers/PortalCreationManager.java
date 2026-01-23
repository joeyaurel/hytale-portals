package dev.joeyaurel.hytale.portals.domain.managers;

import java.util.List;
import java.util.UUID;

public class PortalCreationManager {
    private final List<UUID> playersCreatingPortals;

    public PortalCreationManager() {
        playersCreatingPortals = List.of();
    }

    public void addPlayerCreatingPortal(UUID playerId) {
        playersCreatingPortals.add(playerId);
    }

    public void removePlayerCreatingPortal(UUID playerId) {
        playersCreatingPortals.remove(playerId);
    }

    public boolean isPlayerCreatingPortal(UUID playerId) {
        return playersCreatingPortals.contains(playerId);
    }
}

package dev.joeyaurel.hytale.portals.domain.managers;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public class PortalCreationManager {
    private final List<UUID> playersCreatingPortals;

    @Inject
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

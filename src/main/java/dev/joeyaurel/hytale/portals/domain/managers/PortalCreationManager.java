package dev.joeyaurel.hytale.portals.domain.managers;

import javax.inject.Inject;
import javax.inject.Singleton;
import dev.joeyaurel.hytale.portals.domain.dto.PortalCreateDto;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class PortalCreationManager {

    private final Map<UUID, PortalCreateDto> portalCreateDtos;

    @Inject
    public PortalCreationManager() {
        this.portalCreateDtos = new ConcurrentHashMap<>();
    }

    public PortalCreateDto getPortalCreateDto(UUID playerId) {
        return this.portalCreateDtos.getOrDefault(playerId, null);
    }

    public void setPortalCreateDto(UUID playerId, PortalCreateDto portalCreateDto) {
        this.portalCreateDtos.put(playerId, portalCreateDto);
    }

    public void removePortalCreateDto(UUID playerId) {
        this.portalCreateDtos.remove(playerId);
    }

    public boolean isPlayerCreatingPortal(UUID playerId) {
        return this.portalCreateDtos.containsKey(playerId);
    }
}

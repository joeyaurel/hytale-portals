package dev.joeyaurel.hytale.portals.managers;

import javax.inject.Inject;
import javax.inject.Singleton;
import dev.joeyaurel.hytale.portals.domain.dto.PortalCreateDto;
import dev.joeyaurel.hytale.portals.geometry.Vector;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.awt.*;
import java.util.ArrayList;
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

    public boolean tryAddPortalBound(UUID playerId, PlayerRef playerRef, UUID worldId, Vector3i targetBlock) {
        if (!this.isPlayerCreatingPortal(playerId)) {
            return false;
        }

        PortalCreateDto portalCreateDto = this.getPortalCreateDto(playerId);

        if (portalCreateDto == null) {
            return false;
        }

        if (portalCreateDto.worldId == null) {
            portalCreateDto.worldId = worldId;
        } else if (portalCreateDto.worldId != worldId) {
            playerRef.sendMessage(Message.raw("Portal creation world changed. Resetting bounds.").color(Color.YELLOW));

            portalCreateDto.worldId = worldId;
            portalCreateDto.bounds = new ArrayList<>();
        }

        if (portalCreateDto.bounds == null) {
            portalCreateDto.bounds = new ArrayList<>();
        }

        if (portalCreateDto.bounds.size() + 1 > 2) {
            playerRef.sendMessage(Message.raw("Portal creation bounds limit of 2 reached. Resetting bounds.").color(Color.YELLOW));

            portalCreateDto.bounds = new ArrayList<>();
        }

        int x = targetBlock.getX();
        int y = targetBlock.getY();
        int z = targetBlock.getZ();

        Vector portalBound = new Vector(x, y, z);
        portalCreateDto.bounds.add(portalBound);

        this.setPortalCreateDto(playerId, portalCreateDto);

        if (portalCreateDto.bounds.size() == 1) {
            playerRef.sendMessage(Message.raw("[A] Portal bound A set. (X: " + x + ", Y: " + y + ", Z: " + z + ") Touch another block to set bound B.").color(Color.GREEN));
            playerRef.sendMessage(Message.raw("Or cancel portal creation with `/portal cancel`.").color(Color.GRAY));
        } else if (portalCreateDto.bounds.size() == 2) {
            playerRef.sendMessage(Message.raw("[B] Portal bound B set. (X: " + x + ", Y: " + y + ", Z: " + z + ") Finish portal creation with `/portal done` at the destination, facing in the correct direction.").color(Color.GREEN));
            playerRef.sendMessage(Message.raw("Or cancel portal creation with `/portal cancel`.").color(Color.GRAY));
        }

        return true;
    }
}

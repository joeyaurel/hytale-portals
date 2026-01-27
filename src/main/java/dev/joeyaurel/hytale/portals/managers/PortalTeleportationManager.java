package dev.joeyaurel.hytale.portals.managers;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.joeyaurel.hytale.portals.domain.entities.Portal;
import dev.joeyaurel.hytale.portals.domain.entities.PortalDestination;
import dev.joeyaurel.hytale.portals.stores.PortalStore;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Singleton
public class PortalTeleportationManager {

    private final HytaleLogger logger;
    private final PortalStore portalStore;

    @Inject
    public PortalTeleportationManager(
            HytaleLogger logger,
            PortalStore portalStore
    ) {
        this.logger = logger;
        this.portalStore = portalStore;
    }

    public void teleportPlayerToPortal(
            @NonNull UUID playerId,
            @NonNull UUID portalId,
            @Nullable Consumer<UUID> onCancel,
            @Nullable Consumer<UUID> onSuccess
    ) {
        PlayerRef playerReference = Universe.get().getPlayer(playerId);

        if (playerReference == null || !playerReference.isValid()) {
            if (onCancel != null) {
                onCancel.accept(playerId);
            }

            return;
        }

        Ref<EntityStore> playerEntityRef = playerReference.getReference();

        if (playerEntityRef == null || !playerEntityRef.isValid()) {
            if (onCancel != null) {
                onCancel.accept(playerId);
            }

            return;
        }

        UUID playerWorldId = playerReference.getWorldUuid();

        if (playerWorldId == null) {
            if (onCancel != null) {
                onCancel.accept(playerId);
            }

            return;
        }

        World playerWorld = Universe.get().getWorld(playerWorldId);

        if (playerWorld == null) {
            if (onCancel != null) {
                onCancel.accept(playerId);
            }

            return;
        }

        Optional<Portal> optionalPortal = this.portalStore.getPortalById(portalId);

        if (optionalPortal.isEmpty()) {
            if (onCancel != null) {
                onCancel.accept(playerId);
            }

            return;
        }

        Portal targetPortal = optionalPortal.get();
        PortalDestination portalDestination = targetPortal.getDestination();

        this.logger.atFine().log("Teleporting player to portal " + targetPortal.getName() + " (ID: " + targetPortal.getId() + ").");

        Vector3d newPosition = new Vector3d(portalDestination.getX(), portalDestination.getY(), portalDestination.getZ());

        Vector3f newHeadRotation = new Vector3f(0, 0, 0);
        newHeadRotation.setYaw(portalDestination.getHeadYaw());
        newHeadRotation.setPitch(portalDestination.getHeadPitch());

        playerWorld.execute(() -> {
            World destinationWorld = Universe.get().getWorld(targetPortal.getWorldId());

            if (destinationWorld == null) {
                playerReference.sendMessage(
                        Message.raw("Destination world not found for portal " + targetPortal.getName()).color(Color.RED)
                );

                if (onCancel != null) {
                    onCancel.accept(playerId);
                }

                return;
            }

            Store<EntityStore> entityStore = playerEntityRef.getStore();

            // Teleport player to destination
            entityStore.putComponent(
                    playerEntityRef,
                    Teleport.getComponentType(),
                    Teleport.createForPlayer(
                            destinationWorld,
                            newPosition,
                            newHeadRotation
                    )
            );

            playerReference.sendMessage(
                    Message.raw("Teleported to " + targetPortal.getName() + "!").color(Color.GREEN)
            );

            this.logger.atInfo().log("Player " + playerId + " has been teleported to portal " + targetPortal.getId() + ".");

            if (onSuccess != null) {
                onSuccess.accept(playerId);
            }
        });
    }
}

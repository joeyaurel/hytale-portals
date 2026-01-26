package dev.joeyaurel.hytale.portals.systems.tick;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.joeyaurel.hytale.portals.geometry.Vector;
import dev.joeyaurel.hytale.portals.domain.entities.Portal;
import dev.joeyaurel.hytale.portals.domain.entities.PortalDestination;
import dev.joeyaurel.hytale.portals.stores.PortalStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.awt.*;
import java.util.*;
import java.util.List;

@Singleton
public class EntryTickingSystem extends EntityTickingSystem<EntityStore> {

    private final HytaleLogger logger;
    private final PortalStore portalStore;

    private final Query<EntityStore> query;

    private final List<UUID> playersInPortal;

    @Inject
    public EntryTickingSystem(HytaleLogger logger, PortalStore portalStore) {
        this.logger = logger;
        this.portalStore = portalStore;

        this.query = Query.and(Player.getComponentType());

        this.playersInPortal = new ArrayList<>();
    }

    @Override
    public void tick(float v, int index, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> entityStore, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> reference = archetypeChunk.getReferenceTo(index);

        if (!reference.isValid()) {
            return;
        }

        PlayerRef playerReference = entityStore.getComponent(reference, PlayerRef.getComponentType());
        Player player = entityStore.getComponent(reference, Player.getComponentType());

        if (playerReference == null || player == null) {
            return;
        }

        if (this.playersInPortal.contains(playerReference.getUuid())) {
            // Do not trigger multiple times while in a portal
            return;
        }

        World playerWorld = player.getWorld();

        if (playerWorld == null) {
            return;
        }

        UUID playerWorldId = playerWorld.getWorldConfig().getUuid();
        Transform playerTransform = playerReference.getTransform();
        Vector3d playerPosition = playerTransform.getPosition();

        Optional<Portal> optionalPortal = this.portalStore.findPortalAtLocation(
                playerWorldId,
                new Vector(
                        playerPosition.getX(),
                        playerPosition.getY(),
                        playerPosition.getZ()
                )
        );

        if (optionalPortal.isEmpty()) {
            // No portal found. Exit early.
            return;
        }

        UUID playerId = playerReference.getUuid();

        // Player is in a portal, so add them to the list
        this.playersInPortal.add(playerId);

        Portal portal = optionalPortal.get();

        this.logger.atFine().log("Player " + playerId + " is in portal " + portal.getId() + ".");

        List<Portal> otherNetworkPortals = this.portalStore.getPortalsInNetwork(portal.getNetworkId()).stream().filter(
                predicate -> !predicate.getId().equals(portal.getId())
        ).toList();

        this.logger.atFine().log("Found " + otherNetworkPortals.size() + " other portals in the network " + portal.getNetworkId() + ".");

        if (otherNetworkPortals.isEmpty()) {
            playerReference.sendMessage(
                    Message.raw("You need at least two portals in your network to use this feature.").color(Color.RED)
            );

            this.playersInPortal.remove(playerId);
            return;
        }

        // @TODO If there are more than one portal, give the player a GUI to choose which portal to teleport to
        Portal otherPortal = otherNetworkPortals.getFirst();
        PortalDestination portalDestination = otherPortal.getDestination();

        this.logger.atFine().log("Teleporting player to portal " + otherPortal.getName() + " (ID: " + otherPortal.getId() + ").");

        Vector3d newPosition = new Vector3d(portalDestination.getX(), portalDestination.getY(), portalDestination.getZ());

        Vector3f newHeadRotation = new Vector3f(0, 0, 0);
        newHeadRotation.setYaw(portalDestination.getHeadYaw());
        newHeadRotation.setPitch(portalDestination.getHeadPitch());

        playerWorld.execute(() -> {
            World destinationWorld = Universe.get().getWorld(otherPortal.getWorldId());

            if (destinationWorld == null) {
                playerReference.sendMessage(
                        Message.raw("Destination world not found for portal " + otherPortal.getName()).color(Color.RED)
                );

                this.playersInPortal.remove(playerId);
                return;
            }

            // Teleport player to destination
            entityStore.putComponent(
                    reference,
                    Teleport.getComponentType(),
                    Teleport.createForPlayer(
                            destinationWorld,
                            newPosition,
                            newHeadRotation
                    )
            );

            // Player no longer in the portal
            this.playersInPortal.remove(playerId);

            playerReference.sendMessage(
                    Message.raw("Teleported to " + otherPortal.getName() + "!").color(Color.GREEN)
            );

            this.logger.atInfo().log("Player " + playerId + " has been teleported to portal " + otherPortal.getId() + ".");
        });
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return this.query;
    }
}

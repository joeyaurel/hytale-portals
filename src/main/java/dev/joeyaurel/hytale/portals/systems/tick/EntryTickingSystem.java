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
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.joeyaurel.hytale.portals.geometry.Vector;
import dev.joeyaurel.hytale.portals.domain.entities.Portal;
import dev.joeyaurel.hytale.portals.managers.PortalTeleportationManager;
import dev.joeyaurel.hytale.portals.stores.PortalStore;
import dev.joeyaurel.hytale.portals.ui.pages.PortalTeleportPage;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class EntryTickingSystem extends EntityTickingSystem<EntityStore> {

    private final HytaleLogger logger;
    private final PortalStore portalStore;
    private final PortalTeleportPage portalTeleportPage;
    private final PortalTeleportationManager portalTeleportationManager;

    private final Query<EntityStore> query;

    private final Map<UUID, Long> playersInPortal;
    private final Set<UUID> playersWithGuiOpen;

    @Inject
    public EntryTickingSystem(
            HytaleLogger logger,
            PortalStore portalStore,
            PortalTeleportPage portalTeleportPage,
            PortalTeleportationManager portalTeleportationManager
    ) {
        this.logger = logger;
        this.portalStore = portalStore;
        this.portalTeleportPage = portalTeleportPage;
        this.portalTeleportationManager = portalTeleportationManager;

        this.query = Query.and(Player.getComponentType());

        this.playersInPortal = new ConcurrentHashMap<>();
        this.playersWithGuiOpen = ConcurrentHashMap.newKeySet();
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

        UUID playerId = playerReference.getUuid();

        if (this.playersWithGuiOpen.contains(playerId)) {
            // Do not trigger if GUI is already open
            return;
        }

        if (this.playersInPortal.containsKey(playerId)) {
            long lastTriggerTime = this.playersInPortal.get(playerId);

            if (System.currentTimeMillis() - lastTriggerTime < 2000) {
                // Do not trigger multiple times while in a portal or shortly after
                return;
            }
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

        // Player is in a portal, so add them to the list
        this.playersInPortal.put(playerId, System.currentTimeMillis());

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

        if (otherNetworkPortals.size() > 1) {
            this.playersWithGuiOpen.add(playerId);

            this.portalTeleportPage.open(
                    playerReference,
                    reference,
                    entityStore,
                    otherNetworkPortals,
                    id -> {
                        this.logger.atFine().log("Removing player " + id + " from in-portal list.");

                        this.playersWithGuiOpen.remove(id);
                        this.playersInPortal.remove(id);

                        // Re-trigger immediately if they are still in a portal? No, let the cooldown handle it.
                        // We put it back with the current time to ensure cooldown applies from dismissal time.
                        this.playersInPortal.put(id, System.currentTimeMillis());
                    }
            );

            return;
        }

        Portal otherPortal = otherNetworkPortals.getFirst();

        this.portalTeleportationManager.teleportPlayerToPortal(
                playerReference.getUuid(),
                otherPortal.getId(),
                id -> {
                    playerReference.sendMessage(
                            Message.raw("Destination world not found for portal " + otherPortal.getName()).color(Color.RED)
                    );

                    this.logger.atFine().log("Removing player " + playerId + " from in-portal list.");

                    this.playersInPortal.remove(playerId);
                },
                id -> {
                    this.logger.atFine().log("Removing player " + playerId + " from in-portal list.");

                    this.playersInPortal.remove(playerId);

                    // Re-trigger immediately if they are still in a portal? No, let the cooldown handle it.
                    // We put it back with the current time to ensure cooldown applies from dismissal time.
                    this.playersInPortal.put(playerId, System.currentTimeMillis());
                }
        );
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return this.query;
    }
}

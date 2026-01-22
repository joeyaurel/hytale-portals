package dev.joeyaurel.hytale.portals.systems.tick;

import com.google.inject.Inject;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.joeyaurel.hytale.portals.database.entities.Portal;
import dev.joeyaurel.hytale.portals.database.entities.PortalDestination;
import dev.joeyaurel.hytale.portals.stores.PortalStore;
import jakarta.inject.Singleton;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.awt.*;
import java.util.*;
import java.util.List;

@Singleton
public class EntryTickingSystem extends EntityTickingSystem<EntityStore> {

    private final PortalStore portalStore;

    private final List<UUID> playersInPortal;

    @Inject
    public EntryTickingSystem(PortalStore portalStore) {
        this.portalStore = portalStore;

        this.playersInPortal = new ArrayList<>();
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
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
                playerPosition.getX(),
                playerPosition.getY(),
                playerPosition.getZ()
        );

        if (optionalPortal.isEmpty()) {
            // No portal found. Exit early.
            return;
        }

        // Player is in a portal, so add them to the list
        this.playersInPortal.add(playerReference.getUuid());

        Portal portal = optionalPortal.get();

        List<Portal> otherNetworkPortals = this.portalStore.getPortalsInNetwork(portal.getNetworkId()).stream().filter(
                predicate -> !predicate.getId().equals(portal.getId())
        ).toList();

        if (otherNetworkPortals.size() < 2) {
            playerReference.sendMessage(
                    Message.raw("You need at least two portals in your network to use this feature.").color(Color.RED)
            );

            return;
        }

        // @TODO If there are more than one portal, give the player a GUI to choose which portal to teleport to
        Portal otherPortal = otherNetworkPortals.getFirst();
        PortalDestination portalDestination = portal.getDestination();

        // Teleport player to destination
        entityStore.putComponent(
                reference,
                Teleport.getComponentType(),
                new Teleport(
                        playerWorld,
                        new Vector3d(portalDestination.getX(), portalDestination.getY(), portalDestination.getZ()),
                        new Vector3f(portalDestination.getRotationX(), portalDestination.getRotationY(), portalDestination.getRotationZ())
                )
        );

        // Player no longer in portal
        this.playersInPortal.remove(playerReference.getUuid());

        playerReference.sendMessage(
                Message.raw("Teleported to " + otherPortal.getName() + "!").color(Color.GREEN)
        );
    }
}

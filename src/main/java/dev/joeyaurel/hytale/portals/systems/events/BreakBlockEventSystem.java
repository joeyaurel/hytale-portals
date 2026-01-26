package dev.joeyaurel.hytale.portals.systems.events;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.joeyaurel.hytale.portals.managers.PortalCreationManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Singleton
public class BreakBlockEventSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {

    private final PortalCreationManager portalCreationManager;

    private final Query<EntityStore> query;

    @Inject
    public BreakBlockEventSystem(PortalCreationManager portalCreationManager) {
        super(BreakBlockEvent.class);

        this.portalCreationManager = portalCreationManager;

        this.query = Query.and(Player.getComponentType());
    }

    @Override
    public void handle(
            final int index,
            @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk,
            @Nonnull final Store<EntityStore> store,
            @Nonnull final CommandBuffer<EntityStore> commandBuffer,
            @Nonnull final BreakBlockEvent event
    ) {
        Ref<EntityStore> reference = archetypeChunk.getReferenceTo(index);
        Player player = store.getComponent(reference, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(reference, PlayerRef.getComponentType());

        if (player == null || playerRef == null) {
            return;
        }

        if (player.getGameMode() != GameMode.Creative) {
            return;
        }

        World playerWorld = player.getWorld();

        if (playerWorld == null) {
            return;
        }

        UUID playerId = playerRef.getUuid();
        UUID worldId = playerWorld.getWorldConfig().getUuid();

        if (this.portalCreationManager.tryAddPortalBound(playerId, playerRef, worldId, event.getTargetBlock())) {
            event.setCancelled(true);
        }
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return this.query;
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(RootDependency.first());
    }
}

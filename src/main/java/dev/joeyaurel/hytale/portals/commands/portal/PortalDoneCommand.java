package dev.joeyaurel.hytale.portals.commands.portal;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.joeyaurel.hytale.portals.domain.dto.PortalCreateDto;
import dev.joeyaurel.hytale.portals.domain.entities.Portal;
import dev.joeyaurel.hytale.portals.managers.PortalCreationManager;
import dev.joeyaurel.hytale.portals.geometry.Vector;
import dev.joeyaurel.hytale.portals.permissions.Permissions;
import dev.joeyaurel.hytale.portals.stores.PortalStore;
import dev.joeyaurel.hytale.portals.utils.RotationUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Singleton
public class PortalDoneCommand extends AbstractAsyncCommand {

    private final PortalCreationManager portalCreationManager;
    private final PortalStore portalStore;

    @Inject
    public PortalDoneCommand(
            PortalCreationManager portalCreationManager,
            PortalStore portalStore
    ) {
        super("done", "Finishes creation process of a portal. Also uses the current players position and direction as the portal's destination.");

        this.portalCreationManager = portalCreationManager;
        this.portalStore = portalStore;

        this.addAliases("d", "finish", "f");
        this.requirePermission(Permissions.PORTAL_CREATE);
    }

    @Nonnull
    @Override
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
        CommandSender sender = commandContext.sender();

        if (!(sender instanceof Player player)) {
            return CompletableFuture.completedFuture(null);
        }

        Ref<EntityStore> reference = player.getReference();

        if (reference == null || !reference.isValid()) {
            return CompletableFuture.completedFuture(null);
        }

        UUID playerId = sender.getUuid();

        if (!this.portalCreationManager.isPlayerCreatingPortal(playerId)) {
            sender.sendMessage(Message.raw("You are currently not creating a portal. Type `/portal create <portal name>` to start.").color(Color.RED));
            return CompletableFuture.completedFuture(null);
        }

        PortalCreateDto portalCreateDto = this.portalCreationManager.getPortalCreateDto(playerId);

        if (portalCreateDto == null) {
            sender.sendMessage(Message.raw("Portal creation data not found. Please restart creating the portal via `/portal create <portal name>`.").color(Color.RED));

            this.portalCreationManager.removePortalCreateDto(playerId);

            return CompletableFuture.completedFuture(null);
        }

        if (portalCreateDto.createdBy != playerId) {
            // This should never happen, but let's make sure
            sender.sendMessage(Message.raw("You are not the creator of this portal.").color(Color.RED));

            this.portalCreationManager.removePortalCreateDto(playerId);

            return CompletableFuture.completedFuture(null);
        }

        if (portalCreateDto.networkId == null) {
            sender.sendMessage(Message.raw("Portal network not set. Please restart creating the portal via `/portal create <portal name>`.").color(Color.RED));

            this.portalCreationManager.removePortalCreateDto(playerId);

            return CompletableFuture.completedFuture(null);
        }

        if (portalCreateDto.bounds.size() != 2) {
            sender.sendMessage(Message.raw("You must set two portal bounds before finishing portal creation.").color(Color.RED));
            return CompletableFuture.completedFuture(null);
        }

        Store<EntityStore> store = reference.getStore();
        World world = store.getExternalData().getWorld();
        UUID worldId = world.getWorldConfig().getUuid();

        if (portalCreateDto.worldId != worldId) {
            sender.sendMessage(Message.raw("Portal creation world changed. Resetting bounds.").color(Color.YELLOW));

            portalCreateDto.bounds = new ArrayList<>();

            this.portalCreationManager.setPortalCreateDto(playerId, portalCreateDto);

            sender.sendMessage(Message.raw("Now touch two blocks to define the portal's physical area.").color(Color.GREEN));
            sender.sendMessage(Message.raw("You can also type `/portal cancel` to cancel the creation of the portal.").color(Color.GREEN));

            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> {
            PlayerRef playerReference = store.getComponent(reference, PlayerRef.getComponentType());

            if (playerReference == null) {
                return;
            }

            Transform transform = playerReference.getTransform();

            Vector3d position = transform.getPosition();
            Vector3f bodyRotation = transform.getRotation();
            Vector3f headRotation = playerReference.getHeadRotation();

            double xCenter = Math.floor(position.x) + 0.5;
            double y = position.y;
            double zCenter = Math.floor(position.z) + 0.5;

            portalCreateDto.destinationPosition = new Vector(xCenter, y, zCenter);
            portalCreateDto.destinationBodyYaw = RotationUtils.snapRotation(bodyRotation.getYaw());
            portalCreateDto.destinationHeadYaw = RotationUtils.snapRotation(headRotation.getYaw());
            portalCreateDto.destinationHeadPitch = 0f; // Maybe something to consider for a later time (`headRotation.getPitch()`)

            Portal portal = this.portalStore.createPortal(portalCreateDto);

            if (portal == null) {
                sender.sendMessage(Message.raw("Failed to create portal. Please try again.").color(Color.RED));
                return;
            }

            this.portalCreationManager.removePortalCreateDto(playerId);

            sender.sendMessage(Message.raw("Portal " + portal.getName() + " created successfully!").color(Color.GREEN));
        }, world);
    }
}

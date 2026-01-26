package dev.joeyaurel.hytale.portals.commands.portal;

import javax.inject.Inject;
import javax.inject.Singleton;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.joeyaurel.hytale.portals.managers.PortalCreationManager;
import dev.joeyaurel.hytale.portals.permissions.Permissions;
import dev.joeyaurel.hytale.portals.ui.pages.PortalCreatePage;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Singleton
public class PortalCreateCommand extends AbstractAsyncCommand {

    private final PortalCreationManager portalCreationManager;
    private final PortalCreatePage portalCreatePage;

    private final RequiredArg<String> portalName;

    @Inject
    public PortalCreateCommand(
            PortalCreationManager portalCreationManager,
            PortalCreatePage portalCreatePage
    ) {
        super("create", "Starts creation process of a new portal.");

        this.portalCreationManager = portalCreationManager;
        this.portalCreatePage = portalCreatePage;

        this.portalName = this.withRequiredArg("name", "Name of the portal.", ArgTypes.STRING);

        this.addAliases("c");
        this.requirePermission(Permissions.PORTAL_CREATE);
    }

    @Nonnull
    @Override
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
        CommandSender sender = commandContext.sender();

        if (!(sender instanceof Player player)) {
            return CompletableFuture.completedFuture(null);
        }

        UUID playerId = sender.getUuid();

        if (this.portalCreationManager.isPlayerCreatingPortal(playerId)) {
            sender.sendMessage(Message.raw("You are already creating a portal. Type `/portal cancel` to cancel the creation of the portal.").color(Color.RED));
            return CompletableFuture.completedFuture(null);
        }

        String portalName = this.portalName.get(commandContext);

        // Remove leading and trailing quotes
        portalName = portalName.replaceAll("^\"|\"$", "");

        String finalPortalName = portalName;

        Ref<EntityStore> reference = player.getReference();

        if (reference == null || !reference.isValid()) {
            return CompletableFuture.completedFuture(null);
        }

        Store<EntityStore> store = reference.getStore();
        World world = store.getExternalData().getWorld();

        return CompletableFuture.runAsync(() -> {
            PlayerRef playerReference = store.getComponent(reference, PlayerRef.getComponentType());

            if (playerReference == null) {
                return;
            }

            this.portalCreatePage.open(playerReference, store, finalPortalName);
        }, world);
    }
}

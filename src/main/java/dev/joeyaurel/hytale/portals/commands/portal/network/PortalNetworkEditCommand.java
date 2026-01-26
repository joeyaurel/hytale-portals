package dev.joeyaurel.hytale.portals.commands.portal.network;

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
import dev.joeyaurel.hytale.portals.domain.entities.Network;
import dev.joeyaurel.hytale.portals.permissions.Permissions;
import dev.joeyaurel.hytale.portals.stores.NetworkStore;
import dev.joeyaurel.hytale.portals.ui.pages.PortalNetworkEditPage;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

@Singleton
public class PortalNetworkEditCommand extends AbstractAsyncCommand {

    private final NetworkStore networkStore;
    private final PortalNetworkEditPage portalNetworkEditPage;

    private final RequiredArg<String> networkName;

    @Inject
    public PortalNetworkEditCommand(
            NetworkStore networkStore,
            PortalNetworkEditPage portalNetworkEditPage
    ) {
        super("edit", "Edit an existing portal network.");

        this.networkStore = networkStore;
        this.portalNetworkEditPage = portalNetworkEditPage;

        this.networkName = this.withRequiredArg("name", "Name of the network to edit.", ArgTypes.STRING);

        this.addAliases("e");
        this.requirePermission(Permissions.NETWORK_UPDATE);
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

        String networkName = this.networkName.get(commandContext);

        // Remove leading and trailing quotes
        networkName = networkName.replaceAll("^\"|\"$", "");

        Network network = this.networkStore.getNetworkByName(networkName);

        if (network == null) {
            sender.sendMessage(Message.raw("Network with name \"" + networkName + "\" does not exist.").color(Color.RED));
            return CompletableFuture.completedFuture(null);
        }

        Store<EntityStore> store = reference.getStore();
        World world = store.getExternalData().getWorld();

        return CompletableFuture.runAsync(() -> {
            PlayerRef playerReference = store.getComponent(reference, PlayerRef.getComponentType());

            if (playerReference == null) {
                return;
            }

            this.portalNetworkEditPage.open(playerReference, store, network.getId());
        }, world);
    }
}

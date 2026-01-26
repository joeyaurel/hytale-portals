package dev.joeyaurel.hytale.portals.commands.portal.network;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.joeyaurel.hytale.portals.domain.entities.Network;
import dev.joeyaurel.hytale.portals.permissions.Permissions;
import dev.joeyaurel.hytale.portals.stores.NetworkStore;
import dev.joeyaurel.hytale.portals.stores.PortalStore;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.UUID;

@Singleton
public class PortalNetworkRemoveCommand extends CommandBase {

    private final NetworkStore networkStore;
    private final PortalStore portalStore;

    private final RequiredArg<String> networkName;
    private final FlagArg confirm;

    @Inject
    public PortalNetworkRemoveCommand(
            NetworkStore networkStore,
            PortalStore portalStore
    ) {
        super("remove", "Removes a network of portals.");

        this.networkStore = networkStore;
        this.portalStore = portalStore;

        this.networkName = this.withRequiredArg("name", "Name of the network to remove.", ArgTypes.STRING);
        this.confirm = this.withFlagArg("confirm", "Confirm the removal of the network.");

        this.addAliases("rm");
        this.requirePermission(Permissions.NETWORK_DELETE);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext commandContext) {
        CommandSender sender = commandContext.sender();

        if (!(sender instanceof Player)) {
            return;
        }

        String networkName = this.networkName.get(commandContext);

        // Remove leading and trailing quotes
        networkName = networkName.replaceAll("^\"|\"$", "");

        Network network = this.networkStore.getNetworkByName(networkName);

        if (network == null) {
            sender.sendMessage(Message.raw("Network with name \"" + networkName + "\" not found.").color(Color.RED));
            return;
        }

        boolean isConfirmed = this.confirm.get(commandContext);

        if (!isConfirmed) {
            sender.sendMessage(Message.raw("Are you sure you want to remove this network? Type `/portal network remove \"" + networkName + "\" --confirm` to confirm.").color(Color.RED));
            return;
        }

        UUID networkId = network.getId();

        this.networkStore.removeNetwork(networkId);
        this.portalStore.removePortalsInNetwork(networkId);

        sender.sendMessage(Message.raw("Network \"" + networkName + "\" has been removed.").color(Color.GREEN));
    }
}

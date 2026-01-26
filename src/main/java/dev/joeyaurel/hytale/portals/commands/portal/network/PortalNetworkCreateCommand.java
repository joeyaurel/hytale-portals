package dev.joeyaurel.hytale.portals.commands.portal.network;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.joeyaurel.hytale.portals.domain.dto.NetworkCreateDto;
import dev.joeyaurel.hytale.portals.domain.dto.PortalCreateDto;
import dev.joeyaurel.hytale.portals.domain.entities.Network;
import dev.joeyaurel.hytale.portals.domain.managers.PortalCreationManager;
import dev.joeyaurel.hytale.portals.permissions.Permissions;
import dev.joeyaurel.hytale.portals.stores.NetworkStore;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.UUID;

@Singleton
public class PortalNetworkCreateCommand extends CommandBase {

    private final NetworkStore networkStore;

    private final RequiredArg<String> networkName;

    @Inject
    public PortalNetworkCreateCommand(NetworkStore networkStore) {
        super("create", "Create a new portal network.");

        this.networkStore = networkStore;

        this.networkName = this.withRequiredArg("name", "Name of the network.", ArgTypes.STRING);

        this.addAliases("c");
        this.requirePermission(Permissions.NETWORK_CREATE);
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

        Network existingNetwork = this.networkStore.getNetworkByName(networkName);

        if (existingNetwork != null) {
            sender.sendMessage(Message.raw("A network with that name already exists. (ID: " + existingNetwork.getId() + ")").color(Color.RED));
            return;
        }

        UUID playerId = sender.getUuid();

        NetworkCreateDto networkCreateDto = new NetworkCreateDto();
        networkCreateDto.name = this.networkName.get(commandContext);
        networkCreateDto.createdBy = playerId;

        Network network = this.networkStore.createNetwork(networkCreateDto);

        if (network == null) {
            sender.sendMessage(Message.raw("Failed to create network. Please try again.").color(Color.RED));
            return;
        }

        sender.sendMessage(Message.raw("Network created successfully.").color(Color.GREEN));
    }
}

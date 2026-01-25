package dev.joeyaurel.hytale.portals.commands;

import javax.inject.Inject;
import javax.inject.Singleton;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import dev.joeyaurel.hytale.portals.PortalsPlugin;
import dev.joeyaurel.hytale.portals.commands.portal.PortalCancelCommand;
import dev.joeyaurel.hytale.portals.commands.portal.PortalCreateCommand;
import dev.joeyaurel.hytale.portals.commands.portal.PortalNetworkCommand;

import javax.annotation.Nonnull;

@Singleton
public class PortalCommand extends CommandBase {

    @Inject
    public PortalCommand(
            PortalsPlugin plugin,
            PortalCreateCommand portalCreateCommand,
            PortalCancelCommand portalCancelCommand,
            PortalNetworkCommand portalNetworkCommand
    ) {
        super("portal", "Prints basic infos for the " + plugin.getPluginName() + " plugin.");

        this.addAliases("portals");

        this.addSubCommand(portalCreateCommand);
        this.addSubCommand(portalCancelCommand);
        this.addSubCommand(portalNetworkCommand);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext commandContext) {
        Message usageMessage = this.getUsageString(commandContext.sender());
        commandContext.sendMessage(usageMessage);
    }
}

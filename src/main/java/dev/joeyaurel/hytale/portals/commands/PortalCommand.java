package dev.joeyaurel.hytale.portals.commands;

import javax.inject.Inject;
import javax.inject.Singleton;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import dev.joeyaurel.hytale.portals.PortalsPlugin;
import dev.joeyaurel.hytale.portals.commands.portal.PortalCreateCommand;

import javax.annotation.Nonnull;

@Singleton
public class PortalCommand extends CommandBase {

    private final PortalsPlugin plugin;

    @Inject
    public PortalCommand(
            PortalsPlugin plugin,
            PortalCreateCommand portalCreateCommand
    ) {
        super("portal", "Prints basic infos for the " + plugin.getPluginName() + " plugin.");

        this.plugin = plugin;

        this.addAliases("portals");

        this.addSubCommand(portalCreateCommand);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        ctx.sendMessage(Message.raw(
                "Hello from the " + this.plugin.getPluginName() + " v" + this.plugin.getPluginVersion() + " plugin!"
        ));
    }
}

package dev.joeyaurel.hytale.portals.commands.portal;

import javax.inject.Inject;
import javax.inject.Singleton;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.joeyaurel.hytale.portals.permissions.Permissions;

import javax.annotation.Nonnull;

@Singleton
public class PortalCreateCommand extends CommandBase {

    @Inject
    public PortalCreateCommand() {
        super("create", "Starts creation process of a new portal.");

        this.requirePermission(Permissions.PORTAL_CREATE);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext commandContext) {
        CommandSender sender = commandContext.sender();

        if (!(sender instanceof Player player)) {
            return;
        }

        sender.sendMessage(Message.raw("Portal creation is not yet supported."));
    }
}

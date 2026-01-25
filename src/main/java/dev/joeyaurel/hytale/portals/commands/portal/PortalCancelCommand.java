package dev.joeyaurel.hytale.portals.commands.portal;

import javax.inject.Inject;
import javax.inject.Singleton;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.joeyaurel.hytale.portals.domain.managers.PortalCreationManager;
import dev.joeyaurel.hytale.portals.permissions.Permissions;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.UUID;

@Singleton
public class PortalCancelCommand extends CommandBase {

    private final PortalCreationManager portalCreationManager;

    @Inject
    public PortalCancelCommand(PortalCreationManager portalCreationManager) {
        super("cancel", "Cancels creation process of a new portal.");

        this.portalCreationManager = portalCreationManager;

        this.requirePermission(Permissions.PORTAL_CREATE);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext commandContext) {
        CommandSender sender = commandContext.sender();

        if (!(sender instanceof Player)) {
            return;
        }

        UUID playerId = sender.getUuid();

        if (!this.portalCreationManager.isPlayerCreatingPortal(playerId)) {
            sender.sendMessage(Message.raw("You are currently not creating a portal. Type `/portal create` to start.").color(Color.RED));
            return;
        }

        this.portalCreationManager.removePortalCreateDto(playerId);

        sender.sendMessage(Message.raw("Portal creation canceled.").color(Color.GREEN));
    }
}

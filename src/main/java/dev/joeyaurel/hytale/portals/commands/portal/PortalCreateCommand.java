package dev.joeyaurel.hytale.portals.commands.portal;

import javax.inject.Inject;
import javax.inject.Singleton;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.joeyaurel.hytale.portals.domain.dto.PortalCreateDto;
import dev.joeyaurel.hytale.portals.domain.managers.PortalCreationManager;
import dev.joeyaurel.hytale.portals.permissions.Permissions;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.UUID;

@Singleton
public class PortalCreateCommand extends CommandBase {

    private final PortalCreationManager portalCreationManager;

    private final RequiredArg<String> portalName;

    @Inject
    public PortalCreateCommand(PortalCreationManager portalCreationManager) {
        super("create", "Starts creation process of a new portal.");

        this.portalCreationManager = portalCreationManager;

        this.portalName = this.withRequiredArg("name", "Name of the portal.", ArgTypes.STRING);

        this.addAliases("c");
        this.requirePermission(Permissions.PORTAL_CREATE);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext commandContext) {
        CommandSender sender = commandContext.sender();

        if (!(sender instanceof Player)) {
            return;
        }

        UUID playerId = sender.getUuid();

        if (this.portalCreationManager.isPlayerCreatingPortal(playerId)) {
            sender.sendMessage(Message.raw("You are already creating a portal. Type `/portal cancel` to cancel the creation of the portal.").color(Color.RED));
            return;
        }

        PortalCreateDto portalCreateDto = new PortalCreateDto();
        portalCreateDto.name = this.portalName.get(commandContext);

        this.portalCreationManager.setPortalCreateDto(playerId, portalCreateDto);

        sender.sendMessage(Message.raw("Portal creation started.").color(Color.GREEN));
        sender.sendMessage(Message.raw("Now touch two blocks to define the portal's physical area.").color(Color.GREEN));
        sender.sendMessage(Message.raw("You can also type `/portal cancel` to cancel the creation of the portal.").color(Color.GREEN));
    }
}

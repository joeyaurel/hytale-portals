package dev.joeyaurel.hytale.portals.ui.pages;

import au.ellie.hyui.builders.HyUIPage;
import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.joeyaurel.hytale.portals.domain.entities.Portal;
import dev.joeyaurel.hytale.portals.managers.PortalTeleportationManager;
import org.jspecify.annotations.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Singleton
public class PortalTeleportPage {

    private final HytaleLogger logger;
    private final PortalTeleportationManager portalTeleportationManager;

    @Inject
    public PortalTeleportPage(
            HytaleLogger logger,
            PortalTeleportationManager portalTeleportationManager
    ) {
        this.logger = logger;
        this.portalTeleportationManager = portalTeleportationManager;
    }

    public void open(
            @NonNull PlayerRef playerReference,
            @NonNull Ref<EntityStore> playerEntityRef,
            @NonNull Store<EntityStore> store,
            @NonNull List<Portal> availablePortals,
            @NonNull Consumer<UUID> onDismiss
    ) {
        String portalsOptions = this.buildPortalsString(availablePortals);

        String html = """
                <style>
                  .container {
                    anchor-width: 500;
                    anchor-height: 250;
                  }
                  .section {
                    layout-mode: top;
                    anchor-height: 90;
                  }
                  .section-header {
                    font-size: 14;
                    font-weight: bold;
                    color: #AAAAAA;
                    text-transform: uppercase;
                    anchor-height: 20;
                  }
                  .description {
                    font-size: 14;
                    color: #888888;
                    anchor-height: 25;
                  }
                  .content-spacer {
                    flex-weight: 1;
                  }
                  .button-row {
                    layout-mode: right;
                    anchor-height: 50;
                  }
                  .button-spacer {
                    anchor-width: 20;
                  }
                </style>
                
                <div class="page-overlay">
                    <div class="container" data-hyui-title="Select Destination">
                        <div class="container-contents">
                            <div class="section">
                              <p class="section-header">Choose Destination</p>
                              <p class="description">Select the portal you want to teleport to.</p>
                              <select id="portalSelect">
                                %s
                              </select>
                            </div>
                
                            <div class="content-spacer"></div>
                
                            <div class="button-row">
                              <button id="teleportButton">Teleport</button>
                              <div class="button-spacer"></div>
                              <input type="reset" id="cancelButton" value="Cancel"/>
                            </div>
                        </div>
                    </div>
                </div>
                """.formatted(portalsOptions);

        PageBuilder.pageForPlayer(playerReference)
                .withLifetime(CustomPageLifetime.CantClose)
                .fromHtml(html)
                .addEventListener("cancelButton", CustomUIEventBindingType.Activating, (ignored, context) -> {
                    context.getPage().ifPresent(HyUIPage::close);
                    onDismiss.accept(playerReference.getUuid());
                })
                .addEventListener("teleportButton", CustomUIEventBindingType.Activating, (ignored, context) -> {
                    Optional<String> selectedPortalId = context.getValue("portalSelect", String.class);

                    if (selectedPortalId.isEmpty()) {
                        playerReference.sendMessage(Message.raw("Please select a destination portal.").color(Color.RED));
                        return;
                    }

                    UUID portalId = UUID.fromString(selectedPortalId.get());

                    Portal targetPortal = availablePortals.stream()
                            .filter(p -> p.getId().equals(portalId))
                            .findFirst()
                            .orElse(null);

                    if (targetPortal == null) {
                        playerReference.sendMessage(Message.raw("Selected portal not found.").color(Color.RED));

                        context.getPage().ifPresent(HyUIPage::close);

                        onDismiss.accept(playerReference.getUuid());

                        return;
                    }

                    this.portalTeleportationManager.teleportPlayerToPortal(
                            playerReference.getUuid(),
                            portalId,
                            onDismiss,
                            null
                    );

                    context.getPage().ifPresent(HyUIPage::close);
                    onDismiss.accept(playerReference.getUuid());
                })
                .open(store);
    }

    private String buildPortalsString(List<Portal> portals) {
        return portals.stream()
                .map(portal -> "<option value=\"" + portal.getId() + "\">" + portal.getName() + "</option>")
                .reduce("", String::concat);
    }
}

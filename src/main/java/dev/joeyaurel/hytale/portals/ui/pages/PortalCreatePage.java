package dev.joeyaurel.hytale.portals.ui.pages;

import au.ellie.hyui.builders.HyUIPage;
import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.joeyaurel.hytale.portals.domain.dto.PortalCreateDto;
import dev.joeyaurel.hytale.portals.domain.entities.Network;
import dev.joeyaurel.hytale.portals.managers.PortalCreationManager;
import dev.joeyaurel.hytale.portals.stores.NetworkStore;
import org.jspecify.annotations.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class PortalCreatePage {

    private final NetworkStore networkStore;
    private final PortalCreationManager portalCreationManager;

    @Inject
    public PortalCreatePage(
            NetworkStore networkStore,
            PortalCreationManager portalCreationManager
    ) {
        this.networkStore = networkStore;
        this.portalCreationManager = portalCreationManager;
    }

    public void open(
            @NonNull PlayerRef playerReference,
            @NonNull Store<EntityStore> store,
            @NonNull String portalName
    ) {
        List<Network> networks = this.networkStore.getNetworks();

        if (networks.isEmpty()) {
            playerReference.sendMessage(Message.raw("No portal networks found. Please create a network first.").color(Color.RED));
            return;
        }

        String networksString = this.buildNetworksString(networks);

        String html = """
                <style>
                  .container {
                    anchor-width: 500;
                    anchor-height: 250;
                  }
                  .container-contents {
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
                    <div class="container" data-hyui-title="Create Portal">
                        <div class="container-contents">
                
                            <div class="section">
                              <p class="section-header">Select Network</p>
                              <p class="description">Choose the network this portal should be attached to.</p>
                              <select id="networkSelect">
                                %s
                              </select>
                            </div>
                
                            <div class="content-spacer"></div>
                
                            <div class="button-row">
                              <button id="nextButton">Next</button>
                              <div class="button-spacer"></div>
                              <input type="reset" id="cancelButton" value="Cancel"/>
                            </div>
                        </div>
                    </div>
                </div>
                """.formatted(networksString);

        PageBuilder.pageForPlayer(playerReference)
                .withLifetime(CustomPageLifetime.CanDismiss)
                .fromHtml(html)
                .addEventListener("cancelButton", CustomUIEventBindingType.Activating, (ignored, context) -> {
                    context.getPage().ifPresent(HyUIPage::close);
                })
                .addEventListener("nextButton", CustomUIEventBindingType.Activating, (ignored, context) -> {
                    Optional<String> selectedNetworkId = context.getValue("networkSelect", String.class);

                    if (selectedNetworkId.isEmpty()) {
                        playerReference.sendMessage(Message.raw("Please select a network.").color(Color.RED));
                        return;
                    }

                    UUID networkId = UUID.fromString(selectedNetworkId.get());
                    Network network = this.networkStore.getNetworkById(networkId);

                    if (network == null) {
                        playerReference.sendMessage(Message.raw("Network not found. Please choose a valid network.").color(Color.RED));
                        return;
                    }

                    PortalCreateDto portalCreateDto = new PortalCreateDto();
                    portalCreateDto.name = portalName;
                    portalCreateDto.networkId = network.getId();
                    portalCreateDto.createdBy = playerReference.getUuid();

                    this.portalCreationManager.setPortalCreateDto(playerReference.getUuid(), portalCreateDto);

                    playerReference.sendMessage(Message.raw("Portal creation started for \"" + portalName + "\" in network \"" + network.getName() + "\".").color(Color.GREEN));
                    playerReference.sendMessage(Message.raw("Now touch two blocks to define the portal's physical area.").color(Color.GREEN));
                    playerReference.sendMessage(Message.raw("You can also type `/portal cancel` to cancel the creation of the portal.").color(Color.GREEN));

                    context.getPage().ifPresent(HyUIPage::close);
                })
                .open(store);
    }

    private String buildNetworksString(List<Network> networks) {
        return networks
                .stream()
                .map(network -> "<option value=\"" + network.getId() + "\">" + network.getName() + "</option>")
                .reduce("", String::concat);
    }
}

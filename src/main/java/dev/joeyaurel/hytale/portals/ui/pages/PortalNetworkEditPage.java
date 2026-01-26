package dev.joeyaurel.hytale.portals.ui.pages;

import au.ellie.hyui.builders.HyUIPage;
import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.joeyaurel.hytale.portals.domain.dto.NetworkUpdateDto;
import dev.joeyaurel.hytale.portals.domain.entities.Network;
import dev.joeyaurel.hytale.portals.domain.entities.Portal;
import dev.joeyaurel.hytale.portals.stores.NetworkStore;
import dev.joeyaurel.hytale.portals.stores.PortalStore;
import org.jspecify.annotations.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class PortalNetworkEditPage {

    private final NetworkStore networkStore;
    private final PortalStore portalStore;

    @Inject
    public PortalNetworkEditPage(
            NetworkStore networkStore,
            PortalStore portalStore
    ) {
        this.networkStore = networkStore;
        this.portalStore = portalStore;
    }

    public void open(
            @NonNull PlayerRef playerReference,
            @NonNull Store<EntityStore> store,
            @NonNull UUID networkId
    ) {
        Network network = this.networkStore.getNetworkById(networkId);

        if (network == null) {
            return;
        }

        List<Portal> portals = this.portalStore.getPortalsInNetwork(network.getId());

        String portalsString = this.buildPortalsString(portals);

        String noPortalsString = "<p>No portals in this network.</p>";

        String portalsSelectString = """
                <select id="portalSelect">
                    %s
                </select>
                """;

        String html = """
                <style>
                  .container {
                    //layout-mode: top;
                    anchor-width: 500;
                    anchor-height: 300;
                  }
                  .container-contents {
                    //layout-mode: top;
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
                  .portals-row {
                    layout-mode: left;
                    anchor-height: 25;
                  }
                  .description {
                    font-size: 14;
                    color: #888888;
                    anchor-height: 25;
                  }
                  .delay-display {
                    font-size: 16;
                    color: #FFCC00;
                    anchor-width: 80;
                    text-align: center;
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
                    <div class="container" data-hyui-title="Edit Portal Network">
                        <div class="container-contents">
                
                            <!-- Name Section -->
                            <div class="section">
                              <p class="section-header">Name</p>
                              <p class="description">Set the name of the portal network.</p>
                              <input type="text" id="nameInput" value="%s" />
                            </div>
                
                            <!-- Portals Section -->
                            <div class="section">
                              <p class="section-header">Portals</p>
                              <p class="description">List of portals in this network.</p>
                              <div class="portals-row">
                                %s
                              </div>
                            </div>
                
                            <!-- Flexible spacer to push buttons to bottom -->
                            <div class="content-spacer"></div>
                
                            <!-- Action Buttons -->
                            <div class="button-row">
                              <button id="saveButton">Save Changes</button>
                              <div class="button-spacer"></div>
                              <input type="reset" id="closeButton" value="Cancel"/>
                            </div>
                        </div>
                
                    </div>
                </div>
                """.formatted(
                network.getName(),
                portals.isEmpty() ? noPortalsString : portalsSelectString.formatted(portalsString)
        );

        PageBuilder.pageForPlayer(playerReference)
                .withLifetime(CustomPageLifetime.CanDismiss)
                .fromHtml(html)
                .addEventListener("closeButton", CustomUIEventBindingType.Activating, (ignored, context) -> {
                    context.getPage().ifPresent(HyUIPage::close);
                })
                .addEventListener("saveButton", CustomUIEventBindingType.Activating, (ignored, context) -> {
                    Optional<String> newNetworkName = context.getValue("nameInput", String.class);

                    NetworkUpdateDto networkUpdateDto = new NetworkUpdateDto();
                    networkUpdateDto.id = network.getId();
                    networkUpdateDto.name = newNetworkName.orElse(network.getName());

                    Network newNetwork = this.networkStore.updateNetwork(networkUpdateDto);

                    playerReference.sendMessage(Message.raw("Portal network \"" + network.getName() + "\" has been updated to \"" + newNetwork.getName() + "\".").color(Color.GREEN));

                    context.getPage().ifPresent(HyUIPage::close);
                })
                .open(store);
    }

    private String buildPortalsString(List<Portal> portals) {
        return portals
                .stream()
                .map(portal -> "<option value=\"" + portal.getId() + "\">" + portal.getName() + "</option>")
                .reduce("", String::concat);
    }
}

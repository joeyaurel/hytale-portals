package dev.joeyaurel.hytale.portals.stores;

import javax.inject.Inject;
import javax.inject.Singleton;

import dev.joeyaurel.hytale.portals.domain.dto.NetworkCreateDto;
import dev.joeyaurel.hytale.portals.domain.entities.Network;
import dev.joeyaurel.hytale.portals.database.repositories.NetworkRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Singleton
public class NetworkStore {

    private final NetworkRepository networkRepository;
    private final PortalStore portalStore;

    private List<Network> cachedNetworks;

    @Inject
    public NetworkStore(NetworkRepository networkRepository, PortalStore portalStore) {
        this.networkRepository = networkRepository;
        this.portalStore = portalStore;

        this.cachedNetworks = new ArrayList<>();
    }

    public Network getNetworkByName(String networkName) {
        return this.getNetworks()
                .stream()
                .filter(network -> network.getName().equalsIgnoreCase(networkName))
                .findFirst()
                .orElse(null);
    }

    public List<Network> getNetworks() {
        // Lazy load networks from the repository if not cached
        if (cachedNetworks.isEmpty()) {
            cachedNetworks = networkRepository.listAllNetworks();
        }

        return cachedNetworks;
    }

    public Network createNetwork(NetworkCreateDto networkCreateDto) {
        Network network = this.networkRepository.createNetwork(networkCreateDto);

        if (network == null) {
            return null;
        }

        this.cachedNetworks.add(network);

        return network;
    }

    public void removeNetwork(UUID networkId) {
        this.networkRepository.deleteNetwork(networkId);
        this.cachedNetworks.removeIf(network -> network.getId().equals(networkId));
    }

    public void movePortalToNetwork(UUID portalId, UUID newNetworkId) {
        var optionalPortal = this.portalStore.getPortalById(portalId);

        if (optionalPortal.isEmpty()) {
            return;
        }

        var portal = optionalPortal.get();

        // Verify the target network exists
        var networkExists = this.getNetworks().stream()
                .anyMatch(net -> net.getId().equals(newNetworkId));

        if (!networkExists) {
            return;
        }

        // Update portal's networkId
        portal.setNetworkId(newNetworkId);
        this.portalStore.updatePortal(portal);
    }
}

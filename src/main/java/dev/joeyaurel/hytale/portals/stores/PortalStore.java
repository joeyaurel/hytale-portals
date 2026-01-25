package dev.joeyaurel.hytale.portals.stores;

import javax.inject.Inject;
import javax.inject.Singleton;

import dev.joeyaurel.hytale.portals.domain.dto.PortalCreateDto;
import dev.joeyaurel.hytale.portals.domain.entities.Portal;
import dev.joeyaurel.hytale.portals.database.repositories.PortalRepository;
import dev.joeyaurel.hytale.portals.geometry.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class PortalStore {

    private final PortalRepository portalRepository;

    private List<Portal> cachedPortals;

    @Inject
    public PortalStore(PortalRepository portalRepository) {
        this.portalRepository = portalRepository;

        this.cachedPortals = new ArrayList<>();
    }

    public List<Portal> getPortals() {
        // Lazy load portals from the repository if not cached
        if (cachedPortals.isEmpty()) {
            cachedPortals = portalRepository.listAllPortals();
        }

        return cachedPortals;
    }

    public List<Portal> getPortalsInNetwork(UUID networkId) {
        return getPortals().stream()
                .filter(portal -> portal.getNetworkId().equals(networkId))
                .toList();
    }

    public Optional<Portal> getPortalById(UUID portalId) {
        return getPortals().stream()
                .filter(portal -> portal.getId().equals(portalId))
                .findFirst();
    }

    public Portal createPortal(PortalCreateDto portalCreateDto) {
        Portal portal = this.portalRepository.createPortal(portalCreateDto);

        if (portal == null) {
            return null;
        }

        this.cachedPortals.add(portal);

        return portal;
    }

    public void updatePortal(Portal portal) {
        portalRepository.updatePortal(portal);

        // Update in cache
        cachedPortals.removeIf(p -> p.getId().equals(portal.getId()));
        cachedPortals.add(portal);
    }

    public Optional<Portal> findPortalAtLocation(UUID worldId, Vector location) {
        return getPortals().stream()
                .filter(portal -> portal.getWorldId().equals(worldId))
                .filter(portal -> portal.isInside(location))
                .findFirst();
    }
}

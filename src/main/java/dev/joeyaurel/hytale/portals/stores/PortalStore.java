package dev.joeyaurel.hytale.portals.stores;

import dev.joeyaurel.hytale.portals.database.entities.Portal;
import dev.joeyaurel.hytale.portals.database.entities.PortalBound;
import dev.joeyaurel.hytale.portals.database.repositories.PortalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PortalStore {

    private final PortalRepository portalRepository;

    private List<Portal> cachedPortals;

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

    public Portal createPortal(String name, UUID worldId, UUID networkId, List<PortalBound> bounds, UUID createdBy) {
        var portal = this.portalRepository.createPortal(name, worldId, networkId, bounds, createdBy);

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

    public Optional<Portal> findPortalAtLocation(UUID worldId, double x, double y, double z) {
        return getPortals().stream()
                .filter(portal -> portal.getWorldId().equals(worldId))
                .filter(portal -> isLocationInPortalBounds(portal, x, y, z))
                .findFirst();
    }

    boolean isLocationInPortalBounds(Portal portal, double x, double y, double z) {
        List<PortalBound> bounds = portal.getBounds();

        if (bounds.size() < 2) {
            return false;
        }

        // Get min and max coordinates from all bounds
        int minX = bounds.stream().mapToInt(PortalBound::getX).min().orElse(Integer.MAX_VALUE);
        int maxX = bounds.stream().mapToInt(PortalBound::getX).max().orElse(Integer.MIN_VALUE);
        int minY = bounds.stream().mapToInt(PortalBound::getY).min().orElse(Integer.MAX_VALUE);
        int maxY = bounds.stream().mapToInt(PortalBound::getY).max().orElse(Integer.MIN_VALUE);
        int minZ = bounds.stream().mapToInt(PortalBound::getZ).min().orElse(Integer.MAX_VALUE);
        int maxZ = bounds.stream().mapToInt(PortalBound::getZ).max().orElse(Integer.MIN_VALUE);

        // Check if the location is within the bounds
        return x >= minX && x <= maxX &&
               y >= minY && y <= maxY &&
               z >= minZ && z <= maxZ;
    }
}

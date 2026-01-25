package dev.joeyaurel.hytale.portals.database.repositories;

import javax.inject.Inject;
import javax.inject.Singleton;
import com.hypixel.hytale.logger.HytaleLogger;
import dev.joeyaurel.hytale.portals.database.Database;
import dev.joeyaurel.hytale.portals.domain.dto.PortalCreateDto;
import dev.joeyaurel.hytale.portals.domain.entities.Portal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

@Singleton
public class PortalRepository {

    private final Database database;
    private final HytaleLogger logger;

    @Inject
    public PortalRepository(Database database, HytaleLogger logger) {
        this.database = database;
        this.logger = logger;
    }

    public List<Portal> listAllPortals() {
        // TODO
        this.logger.at(Level.FINE).log("Fetching portals from database...");
        return new ArrayList<>();
    }

    public Portal createPortal(PortalCreateDto portalCreateDto) {
        // TODO
        this.logger.at(Level.FINE).log("Creating portal with data name='" + portalCreateDto.name + "', worldId='" + portalCreateDto.worldId + "', networkId='" + portalCreateDto.networkId + "', bounds='" + portalCreateDto.bounds + "', createdBy='" + portalCreateDto.createdBy + "'...");

        if (!portalCreateDto.isValid()) {
            return null;
        }

        return null;
    }

    public void deletePortal(UUID id) {
        // TODO
        this.logger.at(Level.FINE).log("Deleting network with id '" + id + "'...");
    }

    public void updatePortal(Portal portal) {
        // TODO
        this.logger.at(Level.FINE).log("Updating portal with id '" + portal.getId() + "'...");
    }
}

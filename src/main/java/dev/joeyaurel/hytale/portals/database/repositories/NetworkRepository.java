package dev.joeyaurel.hytale.portals.database.repositories;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hypixel.hytale.logger.HytaleLogger;
import dev.joeyaurel.hytale.portals.database.Database;
import dev.joeyaurel.hytale.portals.domain.entities.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

@Singleton
public class NetworkRepository {

    private final Database database;
    private final HytaleLogger logger;

    @Inject
    public NetworkRepository(Database database, HytaleLogger logger) {
        this.database = database;
        this.logger = logger;
    }

    public List<Network> listAllNetworks() {
        // TODO
        this.logger.at(Level.FINE).log("Fetching networks from database...");
        return new ArrayList<>();
    }

    public Network createNetwork(String name, UUID createdBy) {
        // TODO
        this.logger.at(Level.FINE).log("Creating network with name '" + name + "'...");
        return null;
    }

    public void deleteNetwork(UUID id) {
        // TODO
        this.logger.at(Level.FINE).log("Deleting network with id '" + id + "'...");
    }

    public void updateNetwork(Network network) {
        // TODO
        this.logger.at(Level.FINE).log("Updating network with id '" + network.getId() + "'...");
    }
}

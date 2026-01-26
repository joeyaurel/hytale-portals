package dev.joeyaurel.hytale.portals.database.repositories;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.hypixel.hytale.logger.HytaleLogger;
import dev.joeyaurel.hytale.portals.database.Database;
import dev.joeyaurel.hytale.portals.domain.dto.NetworkCreateDto;
import dev.joeyaurel.hytale.portals.domain.dto.NetworkUpdateDto;
import dev.joeyaurel.hytale.portals.domain.entities.Network;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Singleton
public class NetworkRepository {

    private final Database database;
    private final HytaleLogger logger;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

    @Inject
    public NetworkRepository(Database database, HytaleLogger logger) {
        this.database = database;
        this.logger = logger;
    }

    public Network getNetworkById(UUID id) {
        this.logger.atFine().log("Fetching network with id '" + id + "'...");

        Connection connection = this.database.getConnection();
        String sql = "SELECT id, name, created_by, created_at FROM networks WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToNetworkEntity(resultSet);
                }
            }
        } catch (SQLException e) {
            this.logger.atSevere().log("Error fetching network " + id + ": " + e.getMessage());
        }

        return null;
    }

    public List<Network> listAllNetworks() {
        this.logger.atFine().log("Fetching networks from database...");

        List<Network> networks = new ArrayList<>();

        Connection connection = this.database.getConnection();
        String sql = "SELECT id, name, created_by, created_at FROM networks";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Network network = mapToNetworkEntity(resultSet);
                networks.add(network);
            }
        } catch (SQLException e) {
            this.logger.atSevere().log("Error listing networks: " + e.getMessage());
        }

        return networks;
    }

    public Network createNetwork(NetworkCreateDto networkCreateDto) {
        this.logger.atFine().log("Creating network with name '" + networkCreateDto.name + "'...");

        if (!networkCreateDto.isValid()) {
            return null;
        }

        UUID id = UUID.randomUUID();
        Date createdAt = new Date();

        Connection connection = this.database.getConnection();
        String sql = "INSERT INTO networks (id, name, created_by, created_at) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id.toString());
            statement.setString(2, networkCreateDto.name);
            statement.setString(3, networkCreateDto.createdBy.toString());
            statement.setString(4, this.dateFormat.format(createdAt));
            statement.executeUpdate();

            Network network = new Network();
            network.setId(id);
            network.setName(networkCreateDto.name);
            network.setCreatedBy(networkCreateDto.createdBy);
            network.setCreatedAt(createdAt);

            return network;
        } catch (SQLException e) {
            this.logger.atSevere().log("Error creating network: " + e.getMessage());
            return null;
        }
    }

    public Network updateNetwork(NetworkUpdateDto networkUpdateDto) {
        this.logger.atFine().log("Updating network with id '" + networkUpdateDto.id + "'...");

        Connection connection = this.database.getConnection();
        String sql = "UPDATE networks SET name = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, networkUpdateDto.name);
            statement.setString(2, networkUpdateDto.id.toString());
            statement.executeUpdate();

            return this.getNetworkById(networkUpdateDto.id);
        } catch (SQLException e) {
            this.logger.atSevere().log("Error updating network " + networkUpdateDto.id + ": " + e.getMessage());
            return null;
        }
    }

    public void deleteNetwork(UUID id) {
        this.logger.atFine().log("Deleting network with id '" + id + "'...");

        Connection connection = this.database.getConnection();
        String sql = "DELETE FROM networks WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            this.logger.atSevere().log("Error deleting network " + id + ": " + e.getMessage());
        }
    }

    private Network mapToNetworkEntity(ResultSet resultSet) throws SQLException {
        Network network = new Network();
        network.setId(UUID.fromString(resultSet.getString("id")));
        network.setName(resultSet.getString("name"));
        network.setCreatedBy(UUID.fromString(resultSet.getString("created_by")));

        try {
            network.setCreatedAt(this.dateFormat.parse(resultSet.getString("created_at")));
        } catch (ParseException e) {
            this.logger.atSevere().log("Error parsing date for network " + network.getId() + ": " + e.getMessage());
        }

        return network;
    }
}

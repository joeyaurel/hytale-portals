package dev.joeyaurel.hytale.portals.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hypixel.hytale.logger.HytaleLogger;
import dev.joeyaurel.hytale.portals.utils.FileUtils;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;

@Singleton
public class Database {

    private final HytaleLogger logger;
    private Connection connection;

    @Inject
    public Database(HytaleLogger logger) {
        this.logger = logger;

        this.initDatabase();
    }

    public Connection getConnection() {
        return connection;
    }

    private void initDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");

            this.connection = DriverManager.getConnection("jdbc:sqlite:" + FileUtils.DATABASE_PATH);

            try (Statement statement = this.connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON;");
            } finally {
                this.createTables();
            }
        } catch (Exception e) {
            this.logger.at(Level.SEVERE).log("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        try (Statement statement = this.connection.createStatement()) {
            // Networks table
            statement.execute("CREATE TABLE IF NOT EXISTS networks (" +
                    "id TEXT PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "created_by TEXT NOT NULL," +
                    "created_at TEXT NOT NULL" +
                    ")");

            // Portals table
            statement.execute("CREATE TABLE IF NOT EXISTS portals (" +
                    "id TEXT PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "world_id TEXT NOT NULL," +
                    "network_id TEXT NOT NULL," +
                    "sort_order INTEGER DEFAULT 0," +
                    "created_by TEXT NOT NULL," +
                    "created_at TEXT NOT NULL," +
                    "FOREIGN KEY (network_id) REFERENCES networks(id) ON DELETE CASCADE" +
                    ")");

            // Portal bounds table (composition relationship with portals)
            statement.execute("CREATE TABLE IF NOT EXISTS portal_bounds (" +
                    "id TEXT PRIMARY KEY," +
                    "portal_id TEXT NOT NULL," +
                    "x INTEGER NOT NULL," +
                    "y INTEGER NOT NULL," +
                    "z INTEGER NOT NULL," +
                    "FOREIGN KEY (portal_id) REFERENCES portals(id) ON DELETE CASCADE" +
                    ")");

            // Portal destinations table (one-to-one relationship with portals)
            statement.execute("CREATE TABLE IF NOT EXISTS portal_destinations (" +
                    "id TEXT PRIMARY KEY," +
                    "portal_id TEXT NOT NULL UNIQUE," +
                    "x REAL NOT NULL," +
                    "y REAL NOT NULL," +
                    "z REAL NOT NULL," +
                    "rotation_x REAL NOT NULL," +
                    "rotation_y REAL NOT NULL," +
                    "rotation_z REAL NOT NULL," +
                    "FOREIGN KEY (portal_id) REFERENCES portals(id) ON DELETE CASCADE" +
                    ")");
        }
    }
}

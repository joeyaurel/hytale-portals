package dev.joeyaurel.hytale.portals.dependencyinjection;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.util.Config;
import dev.joeyaurel.hytale.portals.commands.PortalCommand;
import dev.joeyaurel.hytale.portals.commands.portal.PortalCreateCommand;
import dev.joeyaurel.hytale.portals.config.PortalsConfig;
import dev.joeyaurel.hytale.portals.database.Database;
import dev.joeyaurel.hytale.portals.database.repositories.NetworkRepository;
import dev.joeyaurel.hytale.portals.stores.NetworkStore;

public class PortalsModule extends AbstractModule {
    private final HytaleLogger logger;
    private final Config<PortalsConfig> config;

    public PortalsModule(HytaleLogger logger, Config<PortalsConfig> config) {
        this.logger = logger;
        this.config = config;
    }

    @Override
    protected void configure() {
        this.bindLogger();
        this.bindConfig();
        this.bindDatabase();
        this.bindRepositories();
        this.bindStores();
        this.bindCommands();
    }

    private void bindLogger() {
        this.bind(HytaleLogger.class).toInstance(this.logger);
    }

    private void bindConfig() {
        this.bind(new TypeLiteral<Config<PortalsConfig>>(){}).toInstance(this.config);
    }

    private void bindDatabase() {
        bind(Database.class).to(Database.class);
    }

    private void bindRepositories() {
        bind(NetworkRepository.class).to(NetworkRepository.class);
    }

    private void bindStores() {
        bind(NetworkStore.class).to(NetworkStore.class);
    }

    private void bindCommands() {
        bind(PortalCommand.class).to(PortalCommand.class);
        bind(PortalCreateCommand.class).to(PortalCreateCommand.class);
    }
}

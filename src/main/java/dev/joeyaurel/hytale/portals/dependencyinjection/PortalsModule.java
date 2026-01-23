package dev.joeyaurel.hytale.portals.dependencyinjection;

import com.google.inject.AbstractModule;
import com.hypixel.hytale.logger.HytaleLogger;
import dev.joeyaurel.hytale.portals.PortalsPlugin;
import dev.joeyaurel.hytale.portals.commands.PortalCommand;
import dev.joeyaurel.hytale.portals.commands.portal.PortalCreateCommand;
import dev.joeyaurel.hytale.portals.database.Database;
import dev.joeyaurel.hytale.portals.database.repositories.NetworkRepository;
import dev.joeyaurel.hytale.portals.stores.NetworkStore;

public class PortalsModule extends AbstractModule {

    private final HytaleLogger logger;
    private final PortalsPlugin plugin;

    public PortalsModule(HytaleLogger logger, PortalsPlugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        this.logger.atFine().log("Binding dependencies...");

        this.bindPlugin();
        this.bindLogger();
        this.bindDatabase();
        this.bindRepositories();
        this.bindStores();
        this.bindCommands();

        this.logger.atFine().log("Dependencies bound!");
    }

    private void bindPlugin() {
        this.bind(PortalsPlugin.class).toInstance(this.plugin);
    }

    private void bindLogger() {
        this.bind(HytaleLogger.class).toInstance(this.logger);
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

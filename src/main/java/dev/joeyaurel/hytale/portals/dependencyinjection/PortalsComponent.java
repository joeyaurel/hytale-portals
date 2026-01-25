package dev.joeyaurel.hytale.portals.dependencyinjection;

import com.hypixel.hytale.logger.HytaleLogger;
import dev.joeyaurel.hytale.portals.PortalsPlugin;
import dev.joeyaurel.hytale.portals.commands.PortalCommand;
import dev.joeyaurel.hytale.portals.commands.portal.PortalCreateCommand;
import dev.joeyaurel.hytale.portals.database.Database;
import dev.joeyaurel.hytale.portals.database.repositories.NetworkRepository;
import dev.joeyaurel.hytale.portals.database.repositories.PortalRepository;
import dev.joeyaurel.hytale.portals.stores.NetworkStore;
import dev.joeyaurel.hytale.portals.stores.PortalStore;
import dev.joeyaurel.hytale.portals.systems.tick.EntryTickingSystem;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = {PortalsModule.class})
public interface PortalsComponent {
    // Basics
    HytaleLogger logger();
    PortalsPlugin plugin();

    // Database
    Database database();

    // Repositories
    NetworkRepository networkRepository();
    PortalRepository portalRepository();

    // Stores
    NetworkStore networkStore();
    PortalStore portalStore();

    // Commands
    PortalCommand portalCommand();
    PortalCreateCommand portalCreateCommand();

    // Systems
    EntryTickingSystem entryTickingSystem();

    @Component.Builder
    interface Builder {
        PortalsComponent build();
        Builder portalsModule(PortalsModule portalsModule);
    }
}

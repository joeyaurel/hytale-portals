package dev.joeyaurel.hytale.portals.dependencyinjection;

import com.hypixel.hytale.logger.HytaleLogger;
import dev.joeyaurel.hytale.portals.PortalsPlugin;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class PortalsModule {

    private final HytaleLogger logger;
    private final PortalsPlugin plugin;

    public PortalsModule(HytaleLogger logger, PortalsPlugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
    }

    @Provides
    @Singleton
    public HytaleLogger provideLogger() {
        return this.logger;
    }

    @Provides
    @Singleton
    public PortalsPlugin providePlugin() {
        return this.plugin;
    }
}

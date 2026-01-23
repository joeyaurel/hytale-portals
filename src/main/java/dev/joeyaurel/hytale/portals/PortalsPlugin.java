package dev.joeyaurel.hytale.portals;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import dev.joeyaurel.hytale.portals.commands.PortalCommand;
import dev.joeyaurel.hytale.portals.config.PortalsConfig;
import dev.joeyaurel.hytale.portals.dependencyinjection.PortalsModule;

import javax.annotation.Nonnull;

public class PortalsPlugin extends JavaPlugin {

    private final HytaleLogger logger = HytaleLogger.forEnclosingClass();
    private final Injector injector;

    public PortalsPlugin(@Nonnull JavaPluginInit init) {
        super(init);

        Config<PortalsConfig> config = this.withConfig(this.getName(), PortalsConfig.CODEC);
        injector = Guice.createInjector(new PortalsModule(logger, config));

        logger.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        logger.atInfo().log("Setting up plugin " + this.getName());

        this.registerCommands();
    }

    private void registerCommands() {
        this.getCommandRegistry().registerCommand(this.injector.getInstance(PortalCommand.class));
    }
}

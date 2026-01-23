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

    private final String pluginName;
    private final String pluginVersion;
    private final Config<PortalsConfig> config;

    private final Injector injector;

    public PortalsPlugin(@Nonnull JavaPluginInit init) {
        super(init);

        this.pluginName = this.getManifest().getName();
        this.pluginVersion = this.getManifest().getVersion().toString();
        this.config = this.withConfig(this.pluginName, PortalsConfig.CODEC);

        PortalsModule module = new PortalsModule(this.logger, this);
        this.injector = Guice.createInjector(module);

        this.logger.atInfo().log("Hello from " + this.pluginName + " version " + this.pluginVersion + "!");
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public Config<PortalsConfig> getConfig() {
        return config;
    }

    public Injector getInjector() {
        return injector;
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

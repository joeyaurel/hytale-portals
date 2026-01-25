package dev.joeyaurel.hytale.portals;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import dev.joeyaurel.hytale.portals.config.PortalsConfig;
import dev.joeyaurel.hytale.portals.dependencyinjection.DaggerPortalsComponent;
import dev.joeyaurel.hytale.portals.dependencyinjection.PortalsComponent;
import dev.joeyaurel.hytale.portals.dependencyinjection.PortalsModule;
import dev.joeyaurel.hytale.portals.utils.FileUtils;

import javax.annotation.Nonnull;

public class PortalsPlugin extends JavaPlugin {

    private final HytaleLogger logger = HytaleLogger.forEnclosingClass();

    private final String pluginName;
    private final String pluginVersion;
    private final Config<PortalsConfig> config;

    private final PortalsComponent component;

    public PortalsPlugin(@Nonnull JavaPluginInit init) {
        super(init);

        FileUtils.ensureMainDirectory();

        this.pluginName = this.getManifest().getName();
        this.pluginVersion = this.getManifest().getVersion().toString();
        this.config = this.withConfig(this.pluginName, PortalsConfig.CODEC);

        this.component = DaggerPortalsComponent.builder()
                .portalsModule(new PortalsModule(this.logger, this))
                .build();

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

    public PortalsComponent getComponent() {
        return component;
    }

    @Override
    protected void start() {
        this.logger.atInfo().log("Setting up plugin " + this.pluginName + " version " + this.pluginVersion + "...");

        this.config.save();

        // Uncomment the following when developing
        //CommandManager.get().handleCommand(ConsoleSender.INSTANCE, "auth persistence Encrypted");
        //CommandManager.get().handleCommand(ConsoleSender.INSTANCE, "auth login device");

        this.registerSystems();
        this.registerCommands();

        this.logger.atInfo().log("Plugin " + this.pluginName + " setup successfully!");
    }

    private void registerSystems() {
        this.logger.atFine().log("Registering systems...");

        // Events
        this.getEntityStoreRegistry().registerSystem(this.component.breakBlockEventSystem());
        this.getEntityStoreRegistry().registerSystem(this.component.damageBlockEventSystem());

        // Ticks
        this.getEntityStoreRegistry().registerSystem(this.component.entryTickingSystem());
    }

    private void registerCommands() {
        this.logger.atFine().log("Registering commands...");

        this.getCommandRegistry().registerCommand(this.component.portalCommand());
    }
}

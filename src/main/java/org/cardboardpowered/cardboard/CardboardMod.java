package org.cardboardpowered.cardboard;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.cardboardpowered.cardboard.plugin.CardboardPluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Cardboard Fabric Mod Entrypoint
 * Initializes the Bukkit/Spigot/Paper API implementation for Fabric
 * 
 * Minecraft Version: 26.1.2
 */
public class CardboardMod implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Cardboard");
    private static CardboardPluginManager pluginManager;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Cardboard - Bukkit API for Fabric");
        
        // Initialize the plugin manager
        pluginManager = new CardboardPluginManager();
        CardboardAPI.setPluginManager(pluginManager);
        
        // Hook into server startup to load plugins
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        
        LOGGER.info("Cardboard mod initialized successfully");
    }

    /**
     * Called when the server has fully started.
     * Loads and enables Bukkit plugins.
     */
    private void onServerStarted(MinecraftServer server) {
        try {
            LOGGER.info("Loading Bukkit plugins...");
            
            File pluginsDir = new File("plugins");
            if (!pluginsDir.exists()) {
                pluginsDir.mkdirs();
                LOGGER.info("Created plugins directory");
            }
            
            // Load all plugins in the plugins directory
            pluginManager.loadPlugins(pluginsDir);
            
            // Enable STARTUP plugins first
            pluginManager.enablePlugins(PluginLoadOrder.STARTUP);
            LOGGER.info("Enabled STARTUP plugins");
            
            // Enable POST_WORLD plugins after world is ready
            ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server1, resourceManager, success) -> {
                if (success) {
                    pluginManager.enablePlugins(PluginLoadOrder.POSTWORLD);
                    LOGGER.info("Enabled POSTWORLD plugins");
                }
            });
            
            LOGGER.info("Plugin loading initialization complete. {} plugins loaded", 
                pluginManager.getPlugins().size());
            
        } catch (Exception e) {
            LOGGER.error("Failed to load plugins", e);
        }
    }

    /**
     * Gets the global plugin manager instance
     */
    public static CardboardPluginManager getPluginManager() {
        return pluginManager;
    }
}

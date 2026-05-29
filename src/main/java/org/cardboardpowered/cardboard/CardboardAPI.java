package org.cardboardpowered.cardboard;

import org.bukkit.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CardboardAPI - Central access point for Cardboard services
 * Provides static access to plugin management and other Cardboard features
 * 
 * Minecraft Version: 26.1.2
 */
public class CardboardAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger("Cardboard/API");
    private static PluginManager pluginManager;

    /**
     * Get the global PluginManager instance
     */
    public static PluginManager getPluginManager() {
        if (pluginManager == null) {
            LOGGER.warn("PluginManager accessed before initialization");
        }
        return pluginManager;
    }

    /**
     * Set the global PluginManager instance
     * Called during mod initialization
     */
    protected static void setPluginManager(PluginManager manager) {
        CardboardAPI.pluginManager = manager;
        LOGGER.info("CardboardAPI PluginManager initialized");
    }

    /**
     * Check if Cardboard is fully initialized
     */
    public static boolean isInitialized() {
        return pluginManager != null;
    }
}

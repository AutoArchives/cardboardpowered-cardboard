package org.cardboardpowered.cardboard.plugin;

import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Cardboard implementation of the Bukkit PluginManager
 * Manages plugin lifecycle: loading, enabling, disabling, and command registration
 * 
 * Minecraft Version: 26.1.2
 */
public class CardboardPluginManager implements PluginManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("Cardboard/PluginManager");
    
    private final Map<String, Plugin> loadedPlugins = new HashMap<>();
    private final Map<String, List<RegisteredListener>> listeners = new HashMap<>();
    private final CommandMap commandMap = new CommandMap(null);
    private final JavaPluginLoader pluginLoader;

    public CardboardPluginManager() {
        this.pluginLoader = new JavaPluginLoader(null);
    }

    @Override
    public void registerEvents(org.bukkit.event.Listener listener, Plugin plugin) {
        // Register event handlers
        for (java.lang.reflect.Method method : listener.getClass().getMethods()) {
            org.bukkit.event.EventHandler handler = method.getAnnotation(org.bukkit.event.EventHandler.class);
            if (handler != null) {
                String eventName = method.getParameterTypes()[0].getSimpleName();
                listeners.computeIfAbsent(eventName, k -> new ArrayList<>());
            }
        }
    }

    @Override
    public void registerEvent(Class<? extends Event> event, org.bukkit.event.Listener listener, 
                             org.bukkit.event.EventPriority priority, 
                             org.bukkit.plugin.EventExecutor executor, Plugin plugin) {
        // Register individual event
    }

    @Override
    public void registerEvent(Class<? extends Event> event, org.bukkit.event.Listener listener, 
                             org.bukkit.event.EventPriority priority, 
                             org.bukkit.plugin.EventExecutor executor, Plugin plugin, 
                             boolean ignoreCancelled) {
        // Register individual event with cancel handling
    }

    @Override
    public void callEvent(Event event) {
        String eventName = event.getClass().getSimpleName();
        List<RegisteredListener> handlers = listeners.getOrDefault(eventName, new ArrayList<>());
        for (RegisteredListener handler : handlers) {
            try {
                handler.callEvent(event);
            } catch (Exception e) {
                LOGGER.error("Error calling event handler", e);
            }
        }
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        if (plugin == null) {
            return;
        }
        
        try {
            plugin.onDisable();
            loadedPlugins.remove(plugin.getName());
            
            PluginDisableEvent event = new PluginDisableEvent(plugin);
            callEvent(event);
            
            HandlerList.unregisterAll(plugin);
            commandMap.getCommands().removeIf(cmd -> plugin.getName().equals(cmd.getPlugin().getName()));
            
            LOGGER.info("Disabled plugin: {}", plugin.getName());
        } catch (Exception e) {
            LOGGER.error("Failed to disable plugin: {}", plugin.getName(), e);
        }
    }

    @Override
    public void disablePlugins() {
        List<Plugin> plugins = new ArrayList<>(loadedPlugins.values());
        for (Plugin plugin : plugins) {
            disablePlugin(plugin);
        }
    }

    @Override
    public void clearPlugins() {
        disablePlugins();
        loadedPlugins.clear();
    }

    @Override
    public Plugin loadPlugin(File file) throws Exception {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("Plugin file must exist");
        }
        
        Plugin plugin = pluginLoader.loadPlugin(file);
        if (plugin != null) {
            loadedPlugins.put(plugin.getName(), plugin);
            LOGGER.info("Loaded plugin: {}", plugin.getName());
        }
        return plugin;
    }

    @Override
    public Plugin[] loadPlugins(File directory) {
        List<Plugin> loaded = new ArrayList<>();
        
        if (directory == null || !directory.isDirectory()) {
            return new Plugin[0];
        }
        
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null) {
            return new Plugin[0];
        }
        
        for (File file : files) {
            try {
                Plugin plugin = loadPlugin(file);
                if (plugin != null) {
                    loaded.add(plugin);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load plugin from file: {}", file.getName(), e);
            }
        }
        
        return loaded.toArray(new Plugin[0]);
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        if (plugin == null || loadedPlugins.containsKey(plugin.getName())) {
            return;
        }
        
        try {
            plugin.onEnable();
            
            PluginEnableEvent event = new PluginEnableEvent(plugin);
            callEvent(event);
            
            LOGGER.info("Enabled plugin: {}", plugin.getName());
        } catch (Exception e) {
            LOGGER.error("Failed to enable plugin: {}", plugin.getName(), e);
        }
    }

    @Override
    public void enablePlugins(PluginLoadOrder type) {
        for (Plugin plugin : loadedPlugins.values()) {
            PluginDescriptionFile desc = plugin.getDescription();
            if (desc.getLoad() == type) {
                enablePlugin(plugin);
            }
        }
    }

    @Override
    public Plugin getPlugin(String name) {
        return loadedPlugins.get(name);
    }

    @Override
    public Plugin[] getPlugins() {
        return loadedPlugins.values().toArray(new Plugin[0]);
    }

    @Override
    public boolean isPluginEnabled(String name) {
        Plugin plugin = loadedPlugins.get(name);
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public boolean isPluginEnabled(Plugin plugin) {
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public CommandMap getCommandMap() {
        return commandMap;
    }

    @Override
    public void useTimings(boolean use) {
        // Timings not implemented in this version
    }

    @Override
    public Collection<RegisteredListener> createRegisteredListeners(org.bukkit.event.Listener listener, Plugin plugin) {
        return new ArrayList<>();
    }

    @Override
    public Collection<String> getPermissionSubscriptions(String permission) {
        return new ArrayList<>();
    }

    @Override
    public Collection<String> getDefaultPermSubscriptions(boolean op) {
        return new ArrayList<>();
    }

    @Override
    public void subscribeToPermission(String permission, org.bukkit.permissions.Permissible permissible) {
    }

    @Override
    public void unsubscribeFromPermission(String permission, org.bukkit.permissions.Permissible permissible) {
    }

    @Override
    public void subscribeToDefaultPerms(boolean op, org.bukkit.permissions.Permissible permissible) {
    }

    @Override
    public void unsubscribeFromDefaultPerms(boolean op, org.bukkit.permissions.Permissible permissible) {
    }

    @Override
    public org.bukkit.permissions.Permission addPermission(org.bukkit.permissions.Permission perm) {
        return perm;
    }

    @Override
    public void removePermission(org.bukkit.permissions.Permission perm) {
    }

    @Override
    public void removePermission(String name) {
    }

    @Override
    public org.bukkit.permissions.Permission getPermission(String name) {
        return null;
    }

    @Override
    public Collection<org.bukkit.permissions.Permission> getDefaultPermissions(boolean op) {
        return new ArrayList<>();
    }

    @Override
    public void recalculatePermissionDefaults(org.bukkit.permissions.Permission perm) {
    }
}

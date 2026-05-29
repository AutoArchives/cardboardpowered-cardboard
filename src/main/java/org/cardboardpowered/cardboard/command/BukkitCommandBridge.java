package org.cardboardpowered.cardboard.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bridges Bukkit commands into Fabric's Brigadier CommandDispatcher
 * Converts Bukkit CommandMap commands to Fabric brigadier commands
 * 
 * Minecraft Version: 26.1.2
 */
public class BukkitCommandBridge {
    private static final Logger LOGGER = LoggerFactory.getLogger("Cardboard/CommandBridge");
    private final CommandDispatcher<ServerCommandSource> dispatcher;

    public BukkitCommandBridge(CommandDispatcher<ServerCommandSource> dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * Register all Bukkit commands from the plugin manager into Fabric dispatcher
     */
    public void registerBukkitCommands(PluginManager pluginManager) {
        try {
            CommandMap commandMap = pluginManager.getCommandMap();
            
            for (org.bukkit.command.Command command : commandMap.getCommands()) {
                registerBukkitCommand(command);
            }
            
            LOGGER.info("Registered {} Bukkit commands", commandMap.getCommands().size());
        } catch (Exception e) {
            LOGGER.error("Failed to register Bukkit commands", e);
        }
    }

    /**
     * Register a single Bukkit command in Fabric dispatcher
     */
    private void registerBukkitCommand(org.bukkit.command.Command command) {
        try {
            String commandName = command.getName();
            
            // Create a literal argument builder for this command
            LiteralArgumentBuilder<ServerCommandSource> builder = 
                LiteralArgumentBuilder.literal(commandName);
            
            // Set the execution handler
            builder.executes(context -> {
                ServerCommandSource source = context.getSource();
                
                try {
                    // Wrap Minecraft's ServerCommandSource as a Bukkit CommandSender
                    org.bukkit.command.CommandSender sender = wrapCommandSource(source);
                    
                    // Execute the Bukkit command
                    command.execute(sender, commandName, new String[]{});
                    return 1; // Success
                } catch (Exception e) {
                    LOGGER.error("Error executing Bukkit command: {}", commandName, e);
                    return 0; // Failure
                }
            });
            
            // Register aliases
            for (String alias : command.getAliases()) {
                LiteralArgumentBuilder<ServerCommandSource> aliasBuilder = 
                    LiteralArgumentBuilder.literal(alias)
                        .executes(builder.build().getCommand());
                dispatcher.register(aliasBuilder);
            }
            
            // Register the main command
            dispatcher.register(builder);
            
            LOGGER.debug("Registered Bukkit command: {}", commandName);
        } catch (Exception e) {
            LOGGER.error("Failed to register Bukkit command", e);
        }
    }

    /**
     * Wraps Minecraft's ServerCommandSource as a Bukkit CommandSender
     */
    private org.bukkit.command.CommandSender wrapCommandSource(ServerCommandSource source) {
        if (source.getEntity() instanceof net.minecraft.entity.player.ServerPlayerEntity) {
            net.minecraft.entity.player.ServerPlayerEntity player = 
                (net.minecraft.entity.player.ServerPlayerEntity) source.getEntity();
            return new CardboardPlayerAdapter(player);
        } else {
            return new CardboardConsoleAdapter(source.getServer());
        }
    }

    /**
     * Adapter class to wrap a ServerPlayerEntity as a Bukkit Player
     */
    private static class CardboardPlayerAdapter implements org.bukkit.command.CommandSender {
        private final net.minecraft.entity.player.ServerPlayerEntity player;

        CardboardPlayerAdapter(net.minecraft.entity.player.ServerPlayerEntity player) {
            this.player = player;
        }

        @Override
        public void sendMessage(String message) {
            player.sendMessage(net.minecraft.text.Text.of(message), false);
        }

        @Override
        public void sendMessage(String[] messages) {
            for (String message : messages) {
                sendMessage(message);
            }
        }

        @Override
        public String getName() {
            return player.getGameProfile().getName();
        }

        @Override
        public boolean isOp() {
            return player.getServer().getPlayerManager().isOperator(player.getGameProfile());
        }

        @Override
        public void setOp(boolean value) {
            if (value) {
                player.getServer().getPlayerManager().addToOperators(player.getGameProfile());
            } else {
                player.getServer().getPlayerManager().removeFromOperators(player.getGameProfile());
            }
        }

        @Override
        public boolean isPermissionSet(String name) {
            return true;
        }

        @Override
        public boolean isPermissionSet(org.bukkit.permissions.Permission perm) {
            return true;
        }

        @Override
        public boolean hasPermission(String name) {
            return isOp();
        }

        @Override
        public boolean hasPermission(org.bukkit.permissions.Permission perm) {
            return isOp();
        }

        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin) {
            return null;
        }

        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, String name, boolean value) {
            return null;
        }

        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, long ticks) {
            return null;
        }

        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, String name, boolean value, long ticks) {
            return null;
        }

        @Override
        public void removeAttachment(org.bukkit.permissions.PermissionAttachment attachment) {
        }

        @Override
        public void recalculatePermissions() {
        }

        @Override
        public java.util.Set<org.bukkit.permissions.PermissionAttachmentInfo> getEffectivePermissions() {
            return java.util.Collections.emptySet();
        }
    }

    /**
     * Adapter class to wrap MinecraftServer as a Bukkit ConsoleCommandSender
     */
    private static class CardboardConsoleAdapter implements org.bukkit.command.ConsoleCommandSender {
        private final net.minecraft.server.MinecraftServer server;

        CardboardConsoleAdapter(net.minecraft.server.MinecraftServer server) {
            this.server = server;
        }

        @Override
        public void sendMessage(String message) {
            LOGGER.info(message);
        }

        @Override
        public void sendMessage(String[] messages) {
            for (String message : messages) {
                sendMessage(message);
            }
        }

        @Override
        public String getName() {
            return "Console";
        }

        @Override
        public boolean isOp() {
            return true;
        }

        @Override
        public void setOp(boolean value) {
        }

        @Override
        public boolean isPermissionSet(String name) {
            return true;
        }

        @Override
        public boolean isPermissionSet(org.bukkit.permissions.Permission perm) {
            return true;
        }

        @Override
        public boolean hasPermission(String name) {
            return true;
        }

        @Override
        public boolean hasPermission(org.bukkit.permissions.Permission perm) {
            return true;
        }

        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin) {
            return null;
        }

        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, String name, boolean value) {
            return null;
        }

        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, long ticks) {
            return null;
        }

        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, String name, boolean value, long ticks) {
            return null;
        }

        @Override
        public void removeAttachment(org.bukkit.permissions.PermissionAttachment attachment) {
        }

        @Override
        public void recalculatePermissions() {
        }

        @Override
        public java.util.Set<org.bukkit.permissions.PermissionAttachmentInfo> getEffectivePermissions() {
            return java.util.Collections.emptySet();
        }
    }
}

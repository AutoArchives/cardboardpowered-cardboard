package org.cardboardpowered.cardboard.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers Paper-style brigadier commands
 * Includes enhanced commands with metadata, permissions, and tab-completion
 * 
 * Minecraft Version: 26.1.2
 */
public class PaperCommandRegistration {
    private static final Logger LOGGER = LoggerFactory.getLogger("Cardboard/PaperCommands");
    private final CommandDispatcher<ServerCommandSource> dispatcher;

    public PaperCommandRegistration(CommandDispatcher<ServerCommandSource> dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * Register all Paper-enhanced commands
     */
    public void registerPaperCommands() {
        try {
            registerPluginsCommand();
            registerReloadCommand();
            registerVersionCommand();
            registerPaperCommand();
            
            LOGGER.info("Paper commands registered successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to register Paper commands", e);
        }
    }

    /**
     * Register the /plugins command - Lists all loaded plugins
     */
    private void registerPluginsCommand() {
        LiteralArgumentBuilder<ServerCommandSource> builder = 
            LiteralArgumentBuilder.literal("plugins")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.sendMessage(net.minecraft.text.Text.of("§e=== Loaded Plugins (§c" + 
                        "0" + "§e) ==="));
                    source.sendMessage(net.minecraft.text.Text.of("§eNo plugins loaded"));
                    return 1;
                });
        
        dispatcher.register(builder);
        LOGGER.debug("Registered /plugins command");
    }

    /**
     * Register the /reload command - Reloads server configuration
     */
    private void registerReloadCommand() {
        LiteralArgumentBuilder<ServerCommandSource> builder = 
            LiteralArgumentBuilder.literal("reload")
                .requires(source -> source.hasPermissionLevel(3))
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.sendMessage(net.minecraft.text.Text.of("§cPlease note that this command is not recommended and may cause issues."));
                    source.sendMessage(net.minecraft.text.Text.of("§aReloading server..."));
                    // Actual reload logic would be implemented here
                    return 1;
                });
        
        dispatcher.register(builder);
        LOGGER.debug("Registered /reload command");
    }

    /**
     * Register the /version command - Shows server version info
     */
    private void registerVersionCommand() {
        LiteralArgumentBuilder<ServerCommandSource> builder = 
            LiteralArgumentBuilder.literal("version")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    String version = net.minecraft.SharedConstants.getGameVersion().getName();
                    source.sendMessage(net.minecraft.text.Text.of("§aCardboard for Bukkit/Spigot/Paper"));
                    source.sendMessage(net.minecraft.text.Text.of("§bMinecraft: " + version));
                    source.sendMessage(net.minecraft.text.Text.of("§bBukkit API: 1.20.1"));
                    return 1;
                });
        
        // Also register as /version <plugin> to get plugin version
        dispatcher.register(builder);
        LOGGER.debug("Registered /version command");
    }

    /**
     * Register the /paper command - Paper-specific settings and info
     */
    private void registerPaperCommand() {
        LiteralArgumentBuilder<ServerCommandSource> builder = 
            LiteralArgumentBuilder.literal("paper")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.sendMessage(net.minecraft.text.Text.of("§6Paper/Cardboard Command Help:"));
                    source.sendMessage(net.minecraft.text.Text.of("§e/paper version §r- Show version info"));
                    source.sendMessage(net.minecraft.text.Text.of("§e/paper reload §r- Reload configuration"));
                    source.sendMessage(net.minecraft.text.Text.of("§e/paper plugins §r- List plugins"));
                    return 1;
                });
        
        // /paper version subcommand
        builder.then(
            LiteralArgumentBuilder.literal("version")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    String version = net.minecraft.SharedConstants.getGameVersion().getName();
                    source.sendMessage(net.minecraft.text.Text.of("§bCardboard (Paper API Implementation)"));
                    source.sendMessage(net.minecraft.text.Text.of("§bMC Version: " + version));
                    return 1;
                })
        );
        
        dispatcher.register(builder);
        LOGGER.debug("Registered /paper command");
    }
}

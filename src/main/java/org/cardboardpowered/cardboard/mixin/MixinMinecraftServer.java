package org.cardboardpowered.cardboard.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.cardboardpowered.cardboard.CardboardMod;
import org.cardboardpowered.cardboard.command.BukkitCommandBridge;
import org.cardboardpowered.cardboard.command.PaperCommandRegistration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mixin for MinecraftServer to inject Bukkit/Paper command handling
 * 
 * This mixin hooks into the server command registration phase to:
 * 1. Inject Bukkit commands into Fabric's CommandDispatcher
 * 2. Register Paper-style brigadier commands
 * 3. Wire plugin-defined commands
 */
@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Cardboard");
    
    @Shadow
    public abstract CommandDispatcher<ServerCommandSource> getCommandDispatcher();

    /**
     * Inject after Fabric registers vanilla commands.
     * This ensures Bukkit commands are registered with proper precedence.
     */
    @Inject(
        method = "setupServer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/MinecraftServer;loadWorld()V",
            shift = At.Shift.BEFORE
        )
    )
    private void onCommandRegistration(CallbackInfo ci) {
        try {
            LOGGER.info("Registering Bukkit and Paper commands into Fabric dispatcher");
            
            CommandDispatcher<ServerCommandSource> dispatcher = getCommandDispatcher();
            
            // Step 1: Bridge Bukkit commands into Fabric dispatcher
            BukkitCommandBridge bukitBridge = new BukkitCommandBridge(dispatcher);
            bukitBridge.registerBukkitCommands(CardboardMod.getPluginManager());
            LOGGER.info("Bukkit commands registered");
            
            // Step 2: Register Paper-style brigadier commands
            PaperCommandRegistration paperCommands = new PaperCommandRegistration(dispatcher);
            paperCommands.registerPaperCommands();
            LOGGER.info("Paper commands registered");
            
        } catch (Exception e) {
            LOGGER.error("Failed to register Bukkit/Paper commands", e);
        }
    }
}

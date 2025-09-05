/**
 * Cardboard Mod - Copyright (c) 2020-2025
 */
package org.cardboardpowered.mixin.world;

import org.cardboardpowered.interfaces.IChunkTicketType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ChunkTicketType.Use;

/**
 * Mixin for ChunkTicketType
 * 
 * @author Cardboard Mod
 * @implNote ChunkTicketType (Yarn)
 * @implNote TicketType (Paper/Moj)
 * @see {@link https://github.com/PaperMC/Paper/blob/main/paper-server/patches/sources/net/minecraft/server/level/TicketType.java.patch}
 */
@Mixin(ChunkTicketType.class)
public class MixinChunkTicketType implements IChunkTicketType {

    // Bukkit
	private static final ChunkTicketType PLUGIN = register("plugin", 0L, false, Use.LOADING_AND_SIMULATION);
    
	// Paper - start
    private static final ChunkTicketType POST_TELEPORT = register("post_teleport", 5L, false, Use.LOADING_AND_SIMULATION);
    private static final ChunkTicketType PLUGIN_TICKET = register("plugin_ticket", 0L, false, Use.LOADING_AND_SIMULATION);
    private static final ChunkTicketType FUTURE_AWAIT = register("future_await", 0L, false, Use.LOADING_AND_SIMULATION);
    private static final ChunkTicketType CHUNK_LOAD = register("chunk_load", 0L, false, Use.LOADING);
    // Paper - end

    @Override
    public ChunkTicketType getBukkitPluginTicketType() {
        return PLUGIN;
    }
    
    @Shadow
    public static ChunkTicketType register(String id, long expiryTicks, boolean persist, Use use) {
    	return null; // Shadowed
    }

}
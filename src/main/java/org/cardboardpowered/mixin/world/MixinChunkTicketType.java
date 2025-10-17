/**
 * Cardboard Mod - Copyright (c) 2020-2025
 */
package org.cardboardpowered.mixin.world;

import org.cardboardpowered.ChunkTicketBridge;
import org.cardboardpowered.interfaces.IChunkTicketType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.world.ChunkTicketType;
// import net.minecraft.server.world.ChunkTicketType.Use;

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
	private static final ChunkTicketType PLUGIN = ChunkTicketType.register("plugin", 0L, 6);
			// old 1.21.8: register("plugin", 0L, false, Use.LOADING_AND_SIMULATION);

	// Paper - start
    private static final ChunkTicketType POST_TELEPORT = ChunkTicketBridge.POST_TELEPORT;
    private static final ChunkTicketType PLUGIN_TICKET = ChunkTicketBridge.PLUGIN_TICKET;
    private static final ChunkTicketType FUTURE_AWAIT = ChunkTicketBridge.FUTURE_AWAIT;
    private static final ChunkTicketType CHUNK_LOAD = ChunkTicketBridge.CHUNK_LOAD;
    // Paper - end

    @Override
    public ChunkTicketType getBukkitPluginTicketType() {
        return PLUGIN;
    }
    
    @Shadow
    public static ChunkTicketType register(String id, long expiryTicks, int flags) {
    	return null; // Shadowed
    }
    
    /*
    @Shadow
    public static ChunkTicketType register(String id, long expiryTicks, boolean persist, Use use) {
    	return null; // Shadowed
    }
    */

}
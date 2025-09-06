package org.cardboardpowered;

import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ChunkTicketType.Use;

/**
 * Paper's added ChunkTicketType values
 */
public class ChunkTicketBridge {

	// Paper - start
    public static final ChunkTicketType POST_TELEPORT = ChunkTicketType.register("post_teleport", 5L, false, Use.LOADING_AND_SIMULATION);
    public static final ChunkTicketType PLUGIN_TICKET = ChunkTicketType.register("plugin_ticket", 0L, false, Use.LOADING_AND_SIMULATION);
    public static final ChunkTicketType FUTURE_AWAIT = ChunkTicketType.register("future_await", 0L, false, Use.LOADING_AND_SIMULATION);
    public static final ChunkTicketType CHUNK_LOAD = ChunkTicketType.register("chunk_load", 0L, false, Use.LOADING);
    // Paper - end

}
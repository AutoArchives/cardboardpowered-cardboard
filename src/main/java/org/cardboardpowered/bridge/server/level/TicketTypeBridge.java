package org.cardboardpowered.bridge.server.level;

import net.minecraft.server.level.TicketType;

/**
 * Injection Interface for ChunkTicketType.
 * 
 * @see {@link org.cardboardpowered.mixin.world.MixinChunkTicketType}
 */
public interface TicketTypeBridge {

	/**
	 */
    TicketType getBukkitPluginTicketType();

}
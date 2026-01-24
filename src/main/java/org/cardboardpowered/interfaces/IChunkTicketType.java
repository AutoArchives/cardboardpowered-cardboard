package org.cardboardpowered.interfaces;

import net.minecraft.server.level.TicketType;
import net.minecraft.util.Unit;

/**
 * Injection Interface for ChunkTicketType.
 * 
 * @see {@link org.cardboardpowered.mixin.world.MixinChunkTicketType}
 */
public interface IChunkTicketType {

	/**
	 */
    TicketType getBukkitPluginTicketType();

}
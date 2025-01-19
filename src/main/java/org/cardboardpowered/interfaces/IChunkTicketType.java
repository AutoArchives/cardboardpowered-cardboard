package org.cardboardpowered.interfaces;

import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.util.Unit;

/**
 * Injection Interface for ChunkTicketType.
 * 
 * @see {@link org.cardboardpowered.mixin.world.MixinChunkTicketType}
 */
public interface IChunkTicketType {

	/**
	 */
    ChunkTicketType<Unit> getBukkitPluginTicketType();

}
package org.cardboardpowered.interfaces;

import net.minecraft.world.level.block.state.BlockState;

public interface IChunkDeltaUpdateS2CPacket {

	void cardboard$set_block_states(BlockState[] states);

}

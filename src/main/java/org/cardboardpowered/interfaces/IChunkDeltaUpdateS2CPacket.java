package org.cardboardpowered.interfaces;

import net.minecraft.block.BlockState;

public interface IChunkDeltaUpdateS2CPacket {

	void cardboard$set_block_states(BlockState[] states);

}

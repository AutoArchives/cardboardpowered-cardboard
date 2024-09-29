package org.cardboardpowered.mixin.network;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import org.cardboardpowered.interfaces.IChunkDeltaUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkDeltaUpdateS2CPacket.class)
public class MixinChunkDeltaUpdateS2CPacket implements IChunkDeltaUpdateS2CPacket {

    @Shadow
    @Final
    @Mutable
    private BlockState[] blockStates;

    @Override
    public void cardboard$set_block_states(BlockState[] states) {
        this.blockStates = states;
    }
 
}

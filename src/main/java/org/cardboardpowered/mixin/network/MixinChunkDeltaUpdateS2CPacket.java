package org.cardboardpowered.mixin.network;

import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.world.level.block.state.BlockState;
import org.cardboardpowered.interfaces.IChunkDeltaUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientboundSectionBlocksUpdatePacket.class)
public class MixinChunkDeltaUpdateS2CPacket implements IChunkDeltaUpdateS2CPacket {

    @Shadow
    @Final
    @Mutable
    private BlockState[] states;

    @Override
    public void cardboard$set_block_states(BlockState[] states) {
        this.states = states;
    }
 
}

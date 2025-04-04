package org.cardboardpowered.mixin.world;

import org.spongepowered.asm.mixin.Mixin;
import org.cardboardpowered.interfaces.IMixinChunkHolder;
import net.minecraft.server.world.ChunkHolder;

@Mixin(ChunkHolder.class)
public class MixinChunkHolder implements IMixinChunkHolder {
}
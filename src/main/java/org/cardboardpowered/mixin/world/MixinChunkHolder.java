package org.cardboardpowered.mixin.world;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.level.ChunkHolder;
import org.cardboardpowered.interfaces.IMixinChunkHolder;

@Mixin(ChunkHolder.class)
public class MixinChunkHolder implements IMixinChunkHolder {
}
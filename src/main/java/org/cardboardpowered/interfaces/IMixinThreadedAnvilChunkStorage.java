package org.cardboardpowered.interfaces;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.level.ChunkHolder;

public interface IMixinThreadedAnvilChunkStorage {

    Long2ObjectLinkedOpenHashMap<ChunkHolder> getChunkHoldersBF();

}
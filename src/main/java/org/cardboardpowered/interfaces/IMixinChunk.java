/**
 * Cardboard - Paper API for Fabric
 * Copyright (C) 2020-2025
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 3 of the License, or (at your option) any later version.
 */
package org.cardboardpowered.interfaces;

import java.util.Map;

import org.bukkit.Chunk;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registry;
import net.minecraft.world.biome.Biome;

public interface IMixinChunk {

    Chunk getBukkitChunk();

    // Lnet/minecraft/world/chunk/Chunk;blockEntities:Ljava/util/Map;
    //     public final Map<BlockPos, BlockEntity> blockEntities = Maps.newHashMap();

    Map<BlockPos, BlockEntity> cardboard_getBlockEntities();

    default Registry<Biome> bridge$biomeRegistry() {
        return null;
    }
    
}
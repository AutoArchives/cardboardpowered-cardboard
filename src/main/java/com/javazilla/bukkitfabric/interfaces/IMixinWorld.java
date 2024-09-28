/**
 * Cardboard Mod
 * Copyright (C) 2024 <CardboardPowered.org>
 */
package com.javazilla.bukkitfabric.interfaces;

import java.util.Map;

import org.cardboardpowered.impl.block.CapturedBlockState;
import org.cardboardpowered.impl.world.WorldImpl;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.entity.EntityLookup;

public interface IMixinWorld {

    WorldImpl getWorldImpl();

    Map<BlockPos, CapturedBlockState> getCapturedBlockStates_BF();

    boolean isCaptureBlockStates_BF();

    void setCaptureBlockStates_BF(boolean b);

    void set_bukkit_world(WorldImpl world);

	EntityLookup cb$get_entity_lookup();

}
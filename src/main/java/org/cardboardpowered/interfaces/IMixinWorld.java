/**
 * Cardboard Mod
 * Copyright (C) 2024 <CardboardPowered.org>
 */
package org.cardboardpowered.interfaces;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.cardboardpowered.impl.block.CapturedBlockState;
import org.cardboardpowered.impl.world.CraftWorld;

public interface IMixinWorld {

    CraftWorld getCraftWorld();

    Map<BlockPos, CapturedBlockState> getCapturedBlockStates_BF();

    boolean isCaptureBlockStates_BF();

    void setCaptureBlockStates_BF(boolean b);

    void set_bukkit_world(CraftWorld world);

	LevelEntityGetter cb$get_entity_lookup();

}
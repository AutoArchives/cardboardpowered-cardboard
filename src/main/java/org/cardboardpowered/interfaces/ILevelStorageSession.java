package org.cardboardpowered.interfaces;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;

/**
 * @since 1.21.9
 */
public interface ILevelStorageSession {

	void cardboard$set_dimensionType(ResourceKey<LevelStem> value);

	ResourceKey<LevelStem> cardboard$get_dimensionType();

}

package org.cardboardpowered.interfaces;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;

/**
 * @since 1.21.9
 */
public interface ILevelStorageSession {

	void cardboard$set_dimensionType(RegistryKey<DimensionOptions> value);

	RegistryKey<DimensionOptions> cardboard$get_dimensionType();

}

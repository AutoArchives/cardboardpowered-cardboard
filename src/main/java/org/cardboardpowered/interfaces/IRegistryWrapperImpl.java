package org.cardboardpowered.interfaces;

import java.util.Optional;
import net.minecraft.resources.ResourceKey;

public interface IRegistryWrapperImpl<T> {

	/**
	 */
	public Optional<T> getValueForCopying(ResourceKey<T> var1);

}

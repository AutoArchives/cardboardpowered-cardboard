package org.cardboardpowered.interfaces;

import java.util.Optional;

import net.minecraft.registry.RegistryKey;

public interface IRegistryWrapperImpl<T> {

	/**
	 */
	public Optional<T> getValueForCopying(RegistryKey<T> var1);

}

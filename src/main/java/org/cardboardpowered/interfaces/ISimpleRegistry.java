package org.cardboardpowered.interfaces;

import java.util.Map;
import java.util.Optional;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public interface ISimpleRegistry<T> {

	void clearIntrusiveHolder(T instance);

	Map<Identifier, T> cb$temporaryUnfrozenMap();

	/**
	 */
	Optional<T> getValueForCopying(RegistryKey<T> resourceKey);

}

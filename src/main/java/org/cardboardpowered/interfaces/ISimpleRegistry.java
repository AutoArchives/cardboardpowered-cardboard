package org.cardboardpowered.interfaces;

import java.util.Map;

import net.minecraft.util.Identifier;

public interface ISimpleRegistry<T> {

	void clearIntrusiveHolder(T instance);

	Map<Identifier, T> cb$temporaryUnfrozenMap();

}

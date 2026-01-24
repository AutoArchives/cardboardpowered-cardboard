package org.cardboardpowered.interfaces;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;

public interface IComponentChanges{
 
	void copy(DataComponentPatch orig);

	void clear(DataComponentType<?> type);

	boolean isEmpty();

}

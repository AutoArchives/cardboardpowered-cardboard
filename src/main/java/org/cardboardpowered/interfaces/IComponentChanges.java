package org.cardboardpowered.interfaces;

import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentType;

public interface IComponentChanges{
 
	void copy(ComponentChanges orig);

	void clear(DataComponentType<?> type);

	boolean isEmpty();

}

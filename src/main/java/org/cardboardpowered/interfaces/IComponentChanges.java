package org.cardboardpowered.interfaces;

import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;

public interface IComponentChanges{
 
	void copy(ComponentChanges orig);

	void clear(ComponentType<?> type);

	boolean isEmpty();

}

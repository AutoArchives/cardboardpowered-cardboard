/**
 * Cardboard - The Bukkit for Fabric Project
 * Copyright (C) 2020-2025 Isaiah and contributors
 */
package org.cardboardpowered.interfaces;

/**
 * Injection Interface for SaddledComponent.
 * 
 * @see {@link org.cardboardpowered.mixin.entity.MixinSaddledComponent}
 */
public interface ISaddledComponent {

	/**
	 */
    void setBoostTicks(int ticks);

}
/**
 * Cardboard - The Bukkit for Fabric Project
 * Copyright (C) 2020-2025 Isaiah and contributors
 */
package org.cardboardpowered.bridge.world.entity;

/**
 * Injection Interface for SaddledComponent.
 * 
 * @see {@link org.cardboardpowered.mixin.entity.MixinSaddledComponent}
 */
public interface ItemBasedSteeringBridge {

	/**
	 */
    void setBoostTicks(int ticks);

}
package org.cardboardpowered.interfaces;

import net.minecraft.network.chat.Component;

/**
 */
public interface IMixinSignBlockEntity {

	/**
	 */
    Component[] getTextBF();

    /**
     * Note: bukkit adds method.
     */
	boolean cardboard$isFacingFrontText(double x, double z);

}
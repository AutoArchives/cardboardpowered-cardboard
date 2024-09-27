package com.javazilla.bukkitfabric.interfaces;

import net.minecraft.text.Text;

public interface IMixinSignBlockEntity {

    Text[] getTextBF();

    /**
     * Note: bukkit adds method.
     */
	boolean cardboard$isFacingFrontText(double x, double z);

}
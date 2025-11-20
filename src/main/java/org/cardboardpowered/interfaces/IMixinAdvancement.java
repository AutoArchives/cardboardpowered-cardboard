package org.cardboardpowered.interfaces;

import org.bukkit.advancement.Advancement;
import org.bukkit.craftbukkit.advancement.CraftAdvancement;

/**
 * 
 */
public interface IMixinAdvancement {

    CraftAdvancement getBukkitAdvancement();

	Advancement toBukkit();

}
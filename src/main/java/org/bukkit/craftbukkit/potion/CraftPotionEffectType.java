package org.bukkit.craftbukkit.potion;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.potion.PotionEffectType;
import org.cardboardpowered.impl.CardboardPotionEffectType;

public class CraftPotionEffectType extends CardboardPotionEffectType implements Handleable<MobEffect> {

	public CraftPotionEffectType(NamespacedKey key, MobEffect handle) {
		super(key, handle);
	}
	
    public static PotionEffectType minecraftToBukkit(MobEffect minecraft) {
        return (PotionEffectType)CraftRegistry.minecraftToBukkit(minecraft, Registries.MOB_EFFECT);
    }

}

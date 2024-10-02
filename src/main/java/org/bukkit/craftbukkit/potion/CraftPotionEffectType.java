package org.bukkit.craftbukkit.potion;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.potion.PotionEffectType;
import org.cardboardpowered.impl.CardboardPotionEffectType;
import org.jetbrains.annotations.NotNull;

import me.isaiah.common.ICommonMod;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class CraftPotionEffectType extends CardboardPotionEffectType implements Handleable<StatusEffect> {

	public CraftPotionEffectType(NamespacedKey key, StatusEffect handle) {
		super(key, handle);
	}
	
    public static PotionEffectType minecraftToBukkit(StatusEffect minecraft) {
        return (PotionEffectType)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.STATUS_EFFECT, Registry.EFFECT);
    }

}

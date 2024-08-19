package org.bukkit.craftbukkit.potion;

import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.potion.PotionEffectType;
import org.cardboardpowered.impl.CardboardPotionEffectType;
import org.jetbrains.annotations.NotNull;

import me.isaiah.common.ICommonMod;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class CraftPotionEffectType {

	public static PotionEffectType minecraftHolderToBukkit(RegistryEntry<StatusEffect> effectType) {
		// TODO Auto-generated method stub
		return CardboardPotionEffectType.minecraftHolderToBukkit(effectType);
	}

	public static RegistryEntry<StatusEffect> bukkitToMinecraftHolder(@NotNull PotionEffectType type) {
		// TODO Auto-generated method stub

		return CardboardPotionEffectType.bukkitToMinecraftHolder(type);
	}

    public static PotionEffectType minecraftToBukkit(StatusEffect minecraft) {
    	
    	Identifier id = ICommonMod.getIServer().getMinecraft().getRegistryManager().get(RegistryKeys.STATUS_EFFECT).getId(minecraft);
    	return PotionEffectType.getByKey( CraftNamespacedKey.fromMinecraft(id) );
    	
        // return (PotionEffectType)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.STATUS_EFFECT, Registry.EFFECT);
    }

}

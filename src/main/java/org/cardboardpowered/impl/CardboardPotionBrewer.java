package org.cardboardpowered.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import io.papermc.paper.potion.PotionMix;
import me.isaiah.common.ICommonMod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry.Reference;
import net.minecraft.util.Identifier;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

public class CardboardPotionBrewer implements PotionBrewer {

    private static final Map<PotionType, Collection<PotionEffect>> cache = Maps.newHashMap();

    @Override
    public Collection<PotionEffect> getEffects(PotionType damage, boolean upgraded, boolean extended) {
        if (cache.containsKey(damage)) return cache.get(damage);

        String ss = CardboardPotionUtil.fromBukkit(new PotionData(damage, extended, upgraded));
        
        List<StatusEffectInstance> mcEffects = new ArrayList<>();// Potion.byId(ss).getEffects();

        ICommonMod.getIServer().getMinecraft().getRegistryManager().get(RegistryKeys.STATUS_EFFECT);
        Optional<Reference<StatusEffect>> opt = Registries.STATUS_EFFECT.getEntry(Identifier.of(ss));
        
        mcEffects.add(new StatusEffectInstance(opt.get()));
        
        ImmutableList.Builder<PotionEffect> builder = new ImmutableList.Builder<PotionEffect>();
        for (StatusEffectInstance effect : mcEffects) builder.add(CardboardPotionUtil.toBukkit(effect));

        cache.put(damage, builder.build());
        return cache.get(damage);
    }

    @Override
    public Collection<PotionEffect> getEffectsFromDamage(int damage) {
        return new ArrayList<PotionEffect>();
    }

    @SuppressWarnings("deprecation")
    @Override
    public PotionEffect createEffect(PotionEffectType potion, int duration, int amplifier) {
        return new PotionEffect(potion, potion.isInstant() ? 1 : (int) (duration * potion.getDurationModifier()), amplifier);
    }

	@Override
	public void addPotionMix(@NotNull PotionMix arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePotionMix(@NotNull NamespacedKey arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetPotionMixes() {
		// TODO Auto-generated method stub
		
	}

}
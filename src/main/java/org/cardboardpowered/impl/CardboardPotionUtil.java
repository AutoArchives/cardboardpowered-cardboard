package org.cardboardpowered.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

@SuppressWarnings("deprecation")
public class CardboardPotionUtil {

    private static final BiMap<PotionType, String> regular = ImmutableBiMap.<PotionType, String>builder()
            // .put(PotionType.UNCRAFTABLE, "empty")
            .put(PotionType.WATER, "water")
            .put(PotionType.MUNDANE, "mundane")
            .put(PotionType.THICK, "thick")
            .put(PotionType.AWKWARD, "awkward")
            .put(PotionType.NIGHT_VISION, "night_vision")
            .put(PotionType.INVISIBILITY, "invisibility")
            .put(PotionType.LEAPING, "leaping")
            .put(PotionType.FIRE_RESISTANCE, "fire_resistance")
            .put(PotionType.SWIFTNESS, "swiftness")
            .put(PotionType.SLOWNESS, "slowness")
            .put(PotionType.WATER_BREATHING, "water_breathing")
            .put(PotionType.HEALING, "healing")
            .put(PotionType.HARMING, "harming")
            .put(PotionType.POISON, "poison")
            .put(PotionType.REGENERATION, "regeneration")
            .put(PotionType.STRENGTH, "strength")
            .put(PotionType.WEAKNESS, "weakness")
            .put(PotionType.LUCK, "luck")
            .put(PotionType.TURTLE_MASTER, "turtle_master")
            .put(PotionType.SLOW_FALLING, "slow_falling")
            .build();

    private static final BiMap<PotionType, String> upgradeable = ImmutableBiMap.<PotionType, String>builder()
            .put(PotionType.LEAPING, "strong_leaping")
            .put(PotionType.SWIFTNESS, "strong_swiftness")
            .put(PotionType.HEALING, "strong_healing")
            .put(PotionType.HARMING, "strong_harming")
            .put(PotionType.POISON, "strong_poison")
            .put(PotionType.REGENERATION, "strong_regeneration")
            .put(PotionType.STRENGTH, "strong_strength")
            .put(PotionType.SLOWNESS, "strong_slowness")
            .put(PotionType.TURTLE_MASTER, "strong_turtle_master")
            .build();

    private static final BiMap<PotionType, String> extendable = ImmutableBiMap.<PotionType, String>builder()
            .put(PotionType.NIGHT_VISION, "long_night_vision")
            .put(PotionType.INVISIBILITY, "long_invisibility")
            .put(PotionType.LEAPING, "long_leaping")
            .put(PotionType.FIRE_RESISTANCE, "long_fire_resistance")
            .put(PotionType.SWIFTNESS, "long_swiftness")
            .put(PotionType.SLOWNESS, "long_slowness")
            .put(PotionType.WATER_BREATHING, "long_water_breathing")
            .put(PotionType.POISON, "long_poison")
            .put(PotionType.REGENERATION, "long_regeneration")
            .put(PotionType.STRENGTH, "long_strength")
            .put(PotionType.WEAKNESS, "long_weakness")
            .put(PotionType.TURTLE_MASTER, "long_turtle_master")
            .put(PotionType.SLOW_FALLING, "long_slow_falling")
            .build();

    
    
    public static String fromBukkit(PotionData data) {
        String type = data.isUpgraded() ? upgradeable.get(data.getType()) : (data.isExtended() ? extendable.get(data.getType()) : regular.get(data.getType()));
        Preconditions.checkNotNull(type, "Unknown potion type from data " + data);
        return "minecraft:" + type;
    }

    public static PotionData toBukkit(String type) {
        if (type == null) return new PotionData(PotionType.AWKWARD, false, false);
        if (type.startsWith("minecraft:")) type = type.substring(10);

        PotionType potionType = null;
        if ((potionType = extendable.inverse().get(type)) != null)
            return new PotionData(potionType, true, false);

        if ((potionType = upgradeable.inverse().get(type)) != null)
            return new PotionData(potionType, false, true);

        if ((potionType = regular.inverse().get(type)) != null)
            return new PotionData(potionType, false, false);
        return new PotionData(PotionType.AWKWARD, false, false);
    }
    
    public static StatusEffectInstance fromBukkit_New(PotionEffect effect) {
    	RegistryEntry<StatusEffect>  type = CardboardPotionEffectType.bukkitToMinecraftHolder(effect.getType());
        return new StatusEffectInstance(type, effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles());
    }

    public static StatusEffectInstance fromBukkit(PotionEffect effect) {
        return new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(effect.getType().getId()).get(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles());
    }

    public static PotionEffect toBukkit(StatusEffectInstance effect) {
        PotionEffectType type = PotionEffectType.getById(Registries.STATUS_EFFECT.getRawId(effect.getEffectType().value()));
        return new PotionEffect(type, effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles());
    }

    public static boolean equals(StatusEffect mobEffect, PotionEffectType type) {
        return PotionEffectType.getById(Registries.STATUS_EFFECT.getRawId(mobEffect)).equals(type);
    }

}

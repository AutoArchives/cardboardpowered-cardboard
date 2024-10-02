package io.papermc.paper.registry;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.RegistryKeyImpl;
import io.papermc.paper.registry.entry.RegistryEntry;
import io.papermc.paper.world.structure.ConfiguredStructure;
import io.papermc.paper.world.structure.PaperConfiguredStructure;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.structure.Structure;
import org.bukkit.GameEvent;
import org.bukkit.Keyed;
import org.bukkit.MusicInstrument;
import org.bukkit.block.BlockType;
import org.bukkit.craftbukkit.CraftGameEvent;
import org.bukkit.craftbukkit.CraftMusicInstrument;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.damage.CraftDamageType;
import org.bukkit.craftbukkit.generator.structure.CraftStructure;
import org.bukkit.craftbukkit.generator.structure.CraftStructureType;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimMaterial;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimPattern;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.cardboardpowered.impl.CardboardEnchantment;
import org.cardboardpowered.impl.entity.WolfImpl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.VisibleForTesting;

@DefaultQualifier(value=NonNull.class)
public final class PaperRegistries {
    @Deprecated(forRemoval=true)
    @VisibleForTesting
    public static final RegistryKey<ConfiguredStructure> CONFIGURED_STRUCTURE_REGISTRY_KEY = RegistryKeyImpl.createInternal((String)"worldgen/structure");
    
    //@Deprecated(forRemoval=true)
    static final RegistryEntry<Structure, ConfiguredStructure, ?> CONFIGURED_STRUCTURE_REGISTRY_ENTRY = RegistryEntry.entry(
    		RegistryKeys.STRUCTURE, CONFIGURED_STRUCTURE_REGISTRY_KEY, ConfiguredStructure.class, PaperConfiguredStructure::minecraftToBukkit).delayed();
    
    static final List<RegistryEntry<?, ?, ?>> REGISTRY_ENTRIES = List.of(
    		RegistryEntry.entry(RegistryKeys.ENCHANTMENT, RegistryKey.ENCHANTMENT, Enchantment.class, CardboardEnchantment::new)
    				.withSerializationUpdater(FieldRename.ENCHANTMENT_RENAME),
    		RegistryEntry.entry(RegistryKeys.GAME_EVENT, RegistryKey.GAME_EVENT, GameEvent.class, CraftGameEvent::new),
    		RegistryEntry.entry(RegistryKeys.INSTRUMENT, RegistryKey.INSTRUMENT, MusicInstrument.class, CraftMusicInstrument::new),
    		RegistryEntry.entry(RegistryKeys.STATUS_EFFECT, RegistryKey.MOB_EFFECT, PotionEffectType.class, CraftPotionEffectType::new),
    		RegistryEntry.entry(RegistryKeys.STRUCTURE_TYPE, RegistryKey.STRUCTURE_TYPE, StructureType.class, CraftStructureType::new),
    		RegistryEntry.entry(RegistryKeys.BLOCK, RegistryKey.BLOCK, BlockType.class, CraftBlockType::new),
    		RegistryEntry.entry(RegistryKeys.ITEM, RegistryKey.ITEM, ItemType.class, CraftItemType::new),
    		RegistryEntry.entry(RegistryKeys.STRUCTURE, RegistryKey.STRUCTURE, Structure.class, CraftStructure::new).delayed(),
    		RegistryEntry.entry(RegistryKeys.TRIM_MATERIAL, RegistryKey.TRIM_MATERIAL, TrimMaterial.class, CraftTrimMaterial::new).delayed(),
    		RegistryEntry.entry(RegistryKeys.TRIM_PATTERN, RegistryKey.TRIM_PATTERN, TrimPattern.class, CraftTrimPattern::new).delayed(),
    		RegistryEntry.entry(RegistryKeys.DAMAGE_TYPE, RegistryKey.DAMAGE_TYPE, DamageType.class, CraftDamageType::new).delayed(),
    		RegistryEntry.entry(RegistryKeys.WOLF_VARIANT, RegistryKey.WOLF_VARIANT, Wolf.Variant.class, WolfImpl.CraftVariant::new).delayed(),
    		RegistryEntry.apiOnly(RegistryKeys.BIOME, RegistryKey.BIOME, () -> org.bukkit.Registry.BIOME),
    		RegistryEntry.apiOnly(RegistryKeys.PAINTING_VARIANT, RegistryKey.PAINTING_VARIANT, () -> org.bukkit.Registry.ART),
    		RegistryEntry.apiOnly(RegistryKeys.ATTRIBUTE, RegistryKey.ATTRIBUTE, () -> org.bukkit.Registry.ATTRIBUTE),
    		RegistryEntry.apiOnly(RegistryKeys.BANNER_PATTERN, RegistryKey.BANNER_PATTERN, () -> org.bukkit.Registry.BANNER_PATTERN),
    		RegistryEntry.apiOnly(RegistryKeys.CAT_VARIANT, RegistryKey.CAT_VARIANT, () -> org.bukkit.Registry.CAT_VARIANT),
    		RegistryEntry.apiOnly(RegistryKeys.ENTITY_TYPE, RegistryKey.ENTITY_TYPE, () -> org.bukkit.Registry.ENTITY_TYPE),
    		RegistryEntry.apiOnly(RegistryKeys.PARTICLE_TYPE, RegistryKey.PARTICLE_TYPE, () -> org.bukkit.Registry.PARTICLE_TYPE),
    		RegistryEntry.apiOnly(RegistryKeys.POTION, RegistryKey.POTION, () -> org.bukkit.Registry.POTION),
    		RegistryEntry.apiOnly(RegistryKeys.SOUND_EVENT, RegistryKey.SOUND_EVENT, () -> org.bukkit.Registry.SOUNDS),
    		RegistryEntry.apiOnly(RegistryKeys.VILLAGER_PROFESSION, RegistryKey.VILLAGER_PROFESSION, () -> org.bukkit.Registry.VILLAGER_PROFESSION),
    		RegistryEntry.apiOnly(RegistryKeys.VILLAGER_TYPE, RegistryKey.VILLAGER_TYPE, () -> org.bukkit.Registry.VILLAGER_TYPE),
    		RegistryEntry.apiOnly(RegistryKeys.MEMORY_MODULE_TYPE, RegistryKey.MEMORY_MODULE_TYPE, () -> (org.bukkit.Registry<MemoryKey<?>>) (org.bukkit.Registry) org.bukkit.Registry.MEMORY_MODULE_TYPE),
    		RegistryEntry.apiOnly(RegistryKeys.FLUID, RegistryKey.FLUID, () -> org.bukkit.Registry.FLUID),
    		RegistryEntry.apiOnly(RegistryKeys.FROG_VARIANT, RegistryKey.FROG_VARIANT, () -> org.bukkit.Registry.FROG_VARIANT),
    		RegistryEntry.apiOnly(RegistryKeys.MAP_DECORATION_TYPE, RegistryKey.MAP_DECORATION_TYPE, () -> org.bukkit.Registry.MAP_DECORATION_TYPE)
    );
    
    
    private static final Map<RegistryKey<?>, RegistryEntry<?, ?, ?>> BY_REGISTRY_KEY;
    
    private static final Map<net.minecraft.registry.RegistryKey<?>, RegistryEntry<?, ?, ?>> BY_RESOURCE_KEY;

    public static <M, T extends Keyed, R extends org.bukkit.Registry<T>> @Nullable RegistryEntry<M, T, R> getEntry(net.minecraft.registry.RegistryKey<? extends Registry<M>> resourceKey) {
        return (RegistryEntry<M, T, R>) BY_RESOURCE_KEY.get(resourceKey);
    }

    public static <M, T extends Keyed, R extends org.bukkit.Registry<T>> @Nullable RegistryEntry<M, T, R> getEntry(RegistryKey<? super T> registryKey) {
        return (RegistryEntry<M, T, R>) BY_REGISTRY_KEY.get(registryKey);
    }

    public static <M, T> RegistryKey<T> fromNms(net.minecraft.registry.RegistryKey<? extends Registry<M>> registryResourceKey) {
        return (RegistryKey<T>) Objects.requireNonNull(BY_RESOURCE_KEY.get(registryResourceKey), String.valueOf(registryResourceKey) + " doesn't have an api RegistryKey").apiKey();
    }

    public static <M, T> net.minecraft.registry.RegistryKey<? extends Registry<M>> toNms(RegistryKey<T> registryKey) {
        return (net.minecraft.registry.RegistryKey<? extends Registry<M>>) Objects.requireNonNull(BY_REGISTRY_KEY.get(registryKey), String.valueOf(registryKey) + " doesn't have an mc registry ResourceKey").mcKey();
    }

    private PaperRegistries() {
    }

    static {
        IdentityHashMap byRegistryKey = new IdentityHashMap(REGISTRY_ENTRIES.size());
        IdentityHashMap byResourceKey = new IdentityHashMap(REGISTRY_ENTRIES.size());
        for (RegistryEntry<?, ?, ?> entry : REGISTRY_ENTRIES) {
            byRegistryKey.put(entry.apiKey(), entry);
            byResourceKey.put(entry.mcKey(), entry);
        }
        BY_REGISTRY_KEY = Collections.unmodifiableMap(byRegistryKey);
        BY_RESOURCE_KEY = Collections.unmodifiableMap(byResourceKey);
    }
}


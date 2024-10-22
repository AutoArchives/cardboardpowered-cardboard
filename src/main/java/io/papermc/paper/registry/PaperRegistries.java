package io.papermc.paper.registry;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.RegistryKeyImpl;
import io.papermc.paper.registry.data.PaperEnchantmentRegistryEntry;
import io.papermc.paper.registry.data.PaperGameEventRegistryEntry;
import io.papermc.paper.registry.entry.RegistryEntry;
import io.papermc.paper.registry.tag.TagKey;
// import io.papermc.paper.world.structure.ConfiguredStructure;
import io.papermc.paper.world.structure.PaperConfiguredStructure;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.kyori.adventure.key.Key;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.structure.Structure;
import org.bukkit.GameEvent;
import org.bukkit.JukeboxSong;
import org.bukkit.Keyed;
import org.bukkit.MusicInstrument;
import org.bukkit.block.BlockType;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.CraftGameEvent;
import org.bukkit.craftbukkit.CraftJukeboxSong;
import org.bukkit.craftbukkit.CraftMusicInstrument;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.block.banner.CraftPatternType;
import org.bukkit.craftbukkit.damage.CraftDamageType;
import org.bukkit.craftbukkit.generator.structure.CraftStructure;
import org.bukkit.craftbukkit.generator.structure.CraftStructureType;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.inventory.CraftMenuType;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimMaterial;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimPattern;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.map.CraftMapCursor;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffectType;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.cardboardpowered.impl.CardboardEnchantment;
import org.cardboardpowered.impl.entity.CardboardCat;
import org.cardboardpowered.impl.entity.CraftFrog;
import org.cardboardpowered.impl.entity.VillagerImpl;
import org.cardboardpowered.impl.entity.WolfImpl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.bukkit.inventory.MenuType;

@DefaultQualifier(value=NonNull.class)
public final class PaperRegistries {
    
	static final List<RegistryEntry<?, ?>> REGISTRY_ENTRIES = List.of(
			RegistryEntry.writable(RegistryKeys.GAME_EVENT, RegistryKey.GAME_EVENT, GameEvent.class, CraftGameEvent::new, PaperGameEventRegistryEntry.PaperBuilder::new),
			RegistryEntry.entry(RegistryKeys.STRUCTURE_TYPE, RegistryKey.STRUCTURE_TYPE, StructureType.class, CraftStructureType::new),
			RegistryEntry.entry(RegistryKeys.INSTRUMENT, RegistryKey.INSTRUMENT, MusicInstrument.class, CraftMusicInstrument::new),
			RegistryEntry.entry(RegistryKeys.STATUS_EFFECT, RegistryKey.MOB_EFFECT, PotionEffectType.class, CraftPotionEffectType::new),
			RegistryEntry.entry(RegistryKeys.BLOCK, RegistryKey.BLOCK, BlockType.class, CraftBlockType::new),
			RegistryEntry.entry(RegistryKeys.ITEM, RegistryKey.ITEM, ItemType.class, CraftItemType::new),
			RegistryEntry.entry(RegistryKeys.CAT_VARIANT, RegistryKey.CAT_VARIANT, Cat.Type.class, CardboardCat.CraftType::new),
			RegistryEntry.entry(RegistryKeys.FROG_VARIANT, RegistryKey.FROG_VARIANT, Frog.Variant.class, CraftFrog.CraftVariant::new),
			RegistryEntry.entry(RegistryKeys.VILLAGER_PROFESSION, RegistryKey.VILLAGER_PROFESSION, Villager.Profession.class, VillagerImpl.CraftProfession::new),
			RegistryEntry.entry(RegistryKeys.VILLAGER_TYPE, RegistryKey.VILLAGER_TYPE, Villager.Type.class, VillagerImpl.CraftType::new),
			RegistryEntry.entry(RegistryKeys.MAP_DECORATION_TYPE, RegistryKey.MAP_DECORATION_TYPE, MapCursor.Type.class, CraftMapCursor.CraftType::new),
			RegistryEntry.entry(RegistryKeys.SCREEN_HANDLER, RegistryKey.MENU, MenuType.class, CraftMenuType::new),
			RegistryEntry.entry(RegistryKeys.STRUCTURE, RegistryKey.STRUCTURE, Structure.class, CraftStructure::new).delayed(),
			RegistryEntry.entry(RegistryKeys.TRIM_MATERIAL, RegistryKey.TRIM_MATERIAL, TrimMaterial.class, CraftTrimMaterial::new).delayed(),
			RegistryEntry.entry(RegistryKeys.TRIM_PATTERN, RegistryKey.TRIM_PATTERN, TrimPattern.class, CraftTrimPattern::new).delayed(),
			RegistryEntry.entry(RegistryKeys.DAMAGE_TYPE, RegistryKey.DAMAGE_TYPE, DamageType.class, CraftDamageType::new).delayed(),
			RegistryEntry.entry(RegistryKeys.WOLF_VARIANT, RegistryKey.WOLF_VARIANT, Wolf.Variant.class, WolfImpl.CraftVariant::new).delayed(),
			RegistryEntry.writable(RegistryKeys.ENCHANTMENT, RegistryKey.ENCHANTMENT, Enchantment.class, CardboardEnchantment::new, PaperEnchantmentRegistryEntry.PaperBuilder::new).withSerializationUpdater(FieldRename.ENCHANTMENT_RENAME).delayed(),
			RegistryEntry.entry(RegistryKeys.JUKEBOX_SONG, RegistryKey.JUKEBOX_SONG, JukeboxSong.class, CraftJukeboxSong::new).delayed(),
			RegistryEntry.entry(RegistryKeys.BANNER_PATTERN, RegistryKey.BANNER_PATTERN, PatternType.class, CraftPatternType::new).delayed(),
			RegistryEntry.apiOnly(RegistryKeys.BIOME, RegistryKey.BIOME, () -> org.bukkit.Registry.BIOME),
			RegistryEntry.apiOnly(RegistryKeys.PAINTING_VARIANT, RegistryKey.PAINTING_VARIANT, () -> org.bukkit.Registry.ART),
			RegistryEntry.apiOnly(RegistryKeys.ATTRIBUTE, RegistryKey.ATTRIBUTE, () -> org.bukkit.Registry.ATTRIBUTE),
			RegistryEntry.apiOnly(RegistryKeys.ENTITY_TYPE, RegistryKey.ENTITY_TYPE, () -> org.bukkit.Registry.ENTITY_TYPE),
			RegistryEntry.apiOnly(RegistryKeys.PARTICLE_TYPE, RegistryKey.PARTICLE_TYPE, () -> org.bukkit.Registry.PARTICLE_TYPE),
			RegistryEntry.apiOnly(RegistryKeys.POTION, RegistryKey.POTION, () -> org.bukkit.Registry.POTION),
			RegistryEntry.apiOnly(RegistryKeys.SOUND_EVENT, RegistryKey.SOUND_EVENT, () -> org.bukkit.Registry.SOUNDS),
			RegistryEntry.apiOnly(RegistryKeys.MEMORY_MODULE_TYPE, RegistryKey.MEMORY_MODULE_TYPE, () -> (org.bukkit.Registry<MemoryKey<?>>) (org.bukkit.Registry) org.bukkit.Registry.MEMORY_MODULE_TYPE),
			RegistryEntry.apiOnly(RegistryKeys.FLUID, RegistryKey.FLUID, () -> org.bukkit.Registry.FLUID));

    private static final Map<RegistryKey<?>, RegistryEntry<?, ?>> BY_REGISTRY_KEY;
    private static final Map<net.minecraft.registry.RegistryKey<?>, RegistryEntry<?, ?>> BY_RESOURCE_KEY;

    public static <M, T extends Keyed> @Nullable RegistryEntry<M, T> getEntry(net.minecraft.registry.RegistryKey<? extends Registry<M>> resourceKey) {
        return (@Nullable RegistryEntry<M, T>) BY_RESOURCE_KEY.get(resourceKey);
    }

    public static <M, T extends Keyed> @Nullable RegistryEntry<M, T> getEntry(RegistryKey<? super T> registryKey) {
        return (@Nullable RegistryEntry<M, T>) BY_REGISTRY_KEY.get(registryKey);
    }

    public static <M, T> RegistryKey<T> registryFromNms(net.minecraft.registry.RegistryKey<? extends Registry<M>> registryResourceKey) {
        return (RegistryKey<T>) Objects.requireNonNull(BY_RESOURCE_KEY.get(registryResourceKey), String.valueOf(registryResourceKey) + " doesn't have an api RegistryKey").apiKey();
    }

    public static <M, T> net.minecraft.registry.RegistryKey<? extends Registry<M>> registryToNms(RegistryKey<T> registryKey) {
        return (net.minecraft.registry.RegistryKey<? extends Registry<M>>) Objects.requireNonNull(BY_REGISTRY_KEY.get(registryKey), String.valueOf(registryKey) + " doesn't have an mc registry ResourceKey").mcKey();
    }

    public static <M, T> TypedKey<T> fromNms(net.minecraft.registry.RegistryKey<M> resourceKey) {
        return TypedKey.create(PaperRegistries.registryFromNms(resourceKey.getRegistryRef()), (Key)CraftNamespacedKey.fromMinecraft(resourceKey.getValue()));
    }

    public static <M, T> net.minecraft.registry.RegistryKey<M> toNms(TypedKey<T> typedKey) {
    	net.minecraft.registry.RegistryKey<? extends Registry<M>> key = PaperRegistries.registryToNms(typedKey.registryKey());
        return net.minecraft.registry.RegistryKey.of(key, CardboardAdventure.asVanilla(typedKey.key()));
    }

    public static <M, T> TagKey<T> fromNms(net.minecraft.registry.tag.TagKey<M> tagKey) {
        return TagKey.create(PaperRegistries.registryFromNms(tagKey.registry()), (Key)CraftNamespacedKey.fromMinecraft(tagKey.id()));
    }

    public static <M, T> net.minecraft.registry.tag.TagKey<M> toNms(TagKey<T> tagKey) {
    	net.minecraft.registry.RegistryKey<? extends Registry<M>> key = PaperRegistries.registryToNms(tagKey.registryKey());
        return net.minecraft.registry.tag.TagKey.of(key, CardboardAdventure.asVanilla(tagKey.key()));
    }

    private PaperRegistries() {
    }

    static {
        IdentityHashMap byRegistryKey = new IdentityHashMap(REGISTRY_ENTRIES.size());
        IdentityHashMap byResourceKey = new IdentityHashMap(REGISTRY_ENTRIES.size());
        for (RegistryEntry<?, ?> entry : REGISTRY_ENTRIES) {
            byRegistryKey.put(entry.apiKey(), entry);
            byResourceKey.put(entry.mcKey(), entry);
        }
        BY_REGISTRY_KEY = Collections.unmodifiableMap(byRegistryKey);
        BY_RESOURCE_KEY = Collections.unmodifiableMap(byResourceKey);
    }
	
}


package org.bukkit.craftbukkit.legacy;

import java.util.function.BiFunction;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.legacy.fieldrename.FieldRenameData;
import org.bukkit.craftbukkit.legacy.reroute.DoNotReroute;
import org.bukkit.craftbukkit.legacy.reroute.InjectPluginVersion;
import org.bukkit.craftbukkit.legacy.reroute.RerouteMethodName;
import org.bukkit.craftbukkit.legacy.reroute.RerouteStatic;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.loot.LootTables;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class FieldRename {
    public static final BiFunction<NamespacedKey, ApiVersion, NamespacedKey> NONE = (namespacedKey, apiVersion) -> namespacedKey;
    private static final FieldRenameData PATTERN_TYPE_DATA = FieldRenameData.Builder.newBuilder().forVersionsBefore(ApiVersion.FIELD_NAME_PARITY).change("DIAGONAL_RIGHT", "DIAGONAL_UP_RIGHT").forAllVersions().change("STRIPE_SMALL", "SMALL_STRIPES").change("DIAGONAL_LEFT_MIRROR", "DIAGONAL_UP_LEFT").change("DIAGONAL_RIGHT_MIRROR", "DIAGONAL_RIGHT").change("CIRCLE_MIDDLE", "CIRCLE").change("RHOMBUS_MIDDLE", "RHOMBUS").change("HALF_VERTICAL_MIRROR", "HALF_VERTICAL_RIGHT").change("HALF_HORIZONTAL_MIRROR", "HALF_HORIZONTAL_BOTTOM").build();
    private static final FieldRenameData ENCHANTMENT_DATA = FieldRenameData.Builder.newBuilder().forAllVersions().change("PROTECTION_ENVIRONMENTAL", "PROTECTION").change("PROTECTION_FIRE", "FIRE_PROTECTION").change("PROTECTION_FALL", "FEATHER_FALLING").change("PROTECTION_EXPLOSIONS", "BLAST_PROTECTION").change("PROTECTION_PROJECTILE", "PROJECTILE_PROTECTION").change("OXYGEN", "RESPIRATION").change("WATER_WORKER", "AQUA_AFFINITY").change("DAMAGE_ALL", "SHARPNESS").change("DAMAGE_UNDEAD", "SMITE").change("DAMAGE_ARTHROPODS", "BANE_OF_ARTHROPODS").change("LOOT_BONUS_MOBS", "LOOTING").change("DIG_SPEED", "EFFICIENCY").change("DURABILITY", "UNBREAKING").change("LOOT_BONUS_BLOCKS", "FORTUNE").change("ARROW_DAMAGE", "POWER").change("ARROW_KNOCKBACK", "PUNCH").change("ARROW_FIRE", "FLAME").change("ARROW_INFINITE", "INFINITY").change("LUCK", "LUCK_OF_THE_SEA").withKeyRename().change("SWEEPING", "SWEEPING_EDGE").build();
    public static final BiFunction<NamespacedKey, ApiVersion, NamespacedKey> ENCHANTMENT_RENAME = ENCHANTMENT_DATA::getReplacement;
    private static final FieldRenameData BIOME_DATA = FieldRenameData.Builder.newBuilder().forAllVersions().withKeyRename().change("NETHER", "NETHER_WASTES").change("TALL_BIRCH_FOREST", "OLD_GROWTH_BIRCH_FOREST").change("GIANT_TREE_TAIGA", "OLD_GROWTH_PINE_TAIGA").change("GIANT_SPRUCE_TAIGA", "OLD_GROWTH_SPRUCE_TAIGA").change("SNOWY_TUNDRA", "SNOWY_PLAINS").change("JUNGLE_EDGE", "SPARSE_JUNGLE").change("STONE_SHORE", "STONY_SHORE").change("MOUNTAINS", "WINDSWEPT_HILLS").change("WOODED_MOUNTAINS", "WINDSWEPT_FOREST").change("GRAVELLY_MOUNTAINS", "WINDSWEPT_GRAVELLY_HILLS").change("SHATTERED_SAVANNA", "WINDSWEPT_SAVANNA").change("WOODED_BADLANDS_PLATEAU", "WOODED_BADLANDS").build();
    public static final BiFunction<NamespacedKey, ApiVersion, NamespacedKey> BIOME_RENAME = BIOME_DATA::getReplacement;
    private static final FieldRenameData ENTITY_TYPE_DATA = FieldRenameData.Builder.newBuilder().forAllVersions().change("PIG_ZOMBIE", "ZOMBIFIED_PIGLIN").change("DROPPED_ITEM", "ITEM").change("LEASH_HITCH", "LEASH_KNOT").change("ENDER_SIGNAL", "EYE_OF_ENDER").change("SPLASH_POTION", "POTION").change("THROWN_EXP_BOTTLE", "EXPERIENCE_BOTTLE").change("PRIMED_TNT", "TNT").change("FIREWORK", "FIREWORK_ROCKET").change("MINECART_COMMAND", "COMMAND_BLOCK_MINECART").change("MINECART_CHEST", "CHEST_MINECART").change("MINECART_FURNACE", "FURNACE_MINECART").change("MINECART_TNT", "TNT_MINECART").change("MINECART_HOPPER", "HOPPER_MINECART").change("MINECART_MOB_SPAWNER", "SPAWNER_MINECART").change("MUSHROOM_COW", "MOOSHROOM").change("SNOWMAN", "SNOW_GOLEM").change("FISHING_HOOK", "FISHING_BOBBER").change("LIGHTNING", "LIGHTNING_BOLT").withKeyRename().change("XP_ORB", "EXPERIENCE_ORB").change("EYE_OF_ENDER_SIGNAL", "EYE_OF_ENDER").change("XP_BOTTLE", "EXPERIENCE_BOTTLE").change("FIREWORKS_ROCKET", "FIREWORK_ROCKET").change("EVOCATION_FANGS", "EVOKER_FANGS").change("EVOCATION_ILLAGER", "EVOKER").change("VINDICATION_ILLAGER", "VINDICATOR").change("ILLUSION_ILLAGER", "ILLUSIONER").change("COMMANDBLOCK_MINECART", "COMMAND_BLOCK_MINECART").change("SNOWMAN", "SNOW_GOLEM").change("VILLAGER_GOLEM", "IRON_GOLEM").change("ENDER_CRYSTAL", "END_CRYSTAL").change("ZOMBIE_PIGMAN", "ZOMBIFIED_PIGLIN").build();
    public static final BiFunction<NamespacedKey, ApiVersion, NamespacedKey> ENTITY_TYPE_RENAME = ENTITY_TYPE_DATA::getReplacement;
    private static final FieldRenameData POTION_EFFECT_TYPE_DATA = FieldRenameData.Builder.newBuilder().forAllVersions().change("SLOW", "SLOWNESS").change("FAST_DIGGING", "HASTE").change("SLOW_DIGGING", "MINING_FATIGUE").change("INCREASE_DAMAGE", "STRENGTH").change("HEAL", "INSTANT_HEALTH").change("HARM", "INSTANT_DAMAGE").change("JUMP", "JUMP_BOOST").change("CONFUSION", "NAUSEA").change("DAMAGE_RESISTANCE", "RESISTANCE").build();
    private static final FieldRenameData POTION_TYPE_DATA = FieldRenameData.Builder.newBuilder().forAllVersions().change("JUMP", "LEAPING").change("SPEED", "SWIFTNESS").change("INSTANT_HEAL", "HEALING").change("INSTANT_DAMAGE", "HARMING").change("REGEN", "REGENERATION").build();
    private static final FieldRenameData MUSIC_INSTRUMENT_DATA = FieldRenameData.Builder.newBuilder().forAllVersions().change("PONDER", "PONDER_GOAT_HORN").change("SING", "SING_GOAT_HORN").change("SEEK", "SEEK_GOAT_HORN").change("ADMIRE", "ADMIRE_GOAT_HORN").change("CALL", "CALL_GOAT_HORN").change("YEARN", "YEARN_GOAT_HORN").change("DREAM", "DREAM_GOAT_HORN").build();
    private static final FieldRenameData PARTICLE_DATA = FieldRenameData.Builder.newBuilder().forAllVersions().change("EXPLOSION_NORMAL", "POOF").change("EXPLOSION_LARGE", "EXPLOSION").change("EXPLOSION_HUGE", "EXPLOSION_EMITTER").change("FIREWORKS_SPARK", "FIREWORK").change("WATER_BUBBLE", "BUBBLE").change("WATER_SPLASH", "SPLASH").change("WATER_WAKE", "FISHING").change("SUSPENDED", "UNDERWATER").change("SUSPENDED_DEPTH", "UNDERWATER").change("CRIT_MAGIC", "ENCHANTED_HIT").change("SMOKE_NORMAL", "SMOKE").change("SMOKE_LARGE", "LARGE_SMOKE").change("SPELL", "EFFECT").change("SPELL_INSTANT", "INSTANT_EFFECT").change("SPELL_MOB", "ENTITY_EFFECT").change("SPELL_WITCH", "WITCH").change("DRIP_WATER", "DRIPPING_WATER").change("DRIP_LAVA", "DRIPPING_LAVA").change("VILLAGER_ANGRY", "ANGRY_VILLAGER").change("VILLAGER_HAPPY", "HAPPY_VILLAGER").change("TOWN_AURA", "MYCELIUM").change("ENCHANTMENT_TABLE", "ENCHANT").change("REDSTONE", "DUST").change("SNOWBALL", "ITEM_SNOWBALL").change("SNOW_SHOVEL", "ITEM_SNOWBALL").change("SLIME", "ITEM_SLIME").change("ITEM_CRACK", "ITEM").change("BLOCK_CRACK", "BLOCK").change("BLOCK_DUST", "BLOCK").change("WATER_DROP", "RAIN").change("MOB_APPEARANCE", "ELDER_GUARDIAN").change("TOTEM", "TOTEM_OF_UNDYING").withKeyRename().change("GUST_EMITTER", "GUST_EMITTER_LARGE").build();
    public static final BiFunction<NamespacedKey, ApiVersion, NamespacedKey> PARTICLE_TYPE_RENAME = PARTICLE_DATA::getReplacement;
    private static final FieldRenameData LOOT_TABLES_DATA = FieldRenameData.Builder.newBuilder().forAllVersions().change("ZOMBIE_PIGMAN", "ZOMBIFIED_PIGLIN").build();
    private static final FieldRenameData ATTRIBUTE_DATA = FieldRenameData.Builder.newBuilder().forAllVersions().withKeyRename().change("HORSE.JUMP_STRENGTH", "GENERIC.JUMP_STRENGTH").build();
    public static final BiFunction<NamespacedKey, ApiVersion, NamespacedKey> ATTRIBUTE_RENAME = ATTRIBUTE_DATA::getReplacement;
    private static final FieldRenameData MAP_CURSOR_TYPE_DATA = FieldRenameData.Builder.newBuilder().forVersionsBefore(ApiVersion.FIELD_NAME_PARITY).change("RED_MARKER", "TARGET_POINT").forAllVersions().change("WHITE_POINTER", "PLAYER").change("GREEN_POINTER", "FRAME").change("RED_POINTER", "RED_MARKER").change("BLUE_POINTER", "BLUE_MARKER").change("WHITE_CROSS", "TARGET_X").change("WHITE_CIRCLE", "PLAYER_OFF_MAP").change("SMALL_WHITE_CIRCLE", "PLAYER_OFF_LIMITS").change("TEMPLE", "MONUMENT").change("DESERT_VILLAGE", "VILLAGE_DESERT").change("PLAINS_VILLAGE", "VILLAGE_PLAINS").change("SAVANNA_VILLAGE", "VILLAGE_SAVANNA").change("SNOWY_VILLAGE", "VILLAGE_SNOWY").change("TAIGA_VILLAGE", "VILLAGE_TAIGA").build();
    private static final FieldRenameData ITEM_FLAG_DATA = FieldRenameData.Builder.newBuilder().forAllVersions().change("HIDE_POTION_EFFECTS", "HIDE_ADDITIONAL_TOOLTIP").build();

    @DoNotReroute
    public static String rename(ApiVersion apiVersion, String owner, String from) {
        if (owner == null) {
            return from;
        }
        return switch (owner) {
            case "org/bukkit/scoreboard/DisplaySlot" -> FieldRename.convertDisplaySlot(from);
            case "org/bukkit/block/banner/PatternType" -> FieldRename.convertPatternTypeName(apiVersion, from);
            case "org/bukkit/enchantments/Enchantment" -> FieldRename.convertEnchantmentName(apiVersion, from);
            case "org/bukkit/block/Biome" -> FieldRename.convertBiomeName(apiVersion, from);
            case "org/bukkit/entity/EntityType" -> FieldRename.convertEntityTypeName(apiVersion, from);
            case "org/bukkit/potion/PotionEffectType" -> FieldRename.convertPotionEffectTypeName(apiVersion, from);
            case "org/bukkit/potion/PotionType" -> FieldRename.convertPotionTypeName(apiVersion, from);
            case "org/bukkit/MusicInstrument" -> FieldRename.convertMusicInstrumentName(apiVersion, from);
            case "org/bukkit/Particle" -> FieldRename.convertParticleName(apiVersion, from);
            case "org/bukkit/loot/LootTables" -> FieldRename.convertLootTablesName(apiVersion, from);
            case "org/bukkit/attribute/Attribute" -> FieldRename.convertAttributeName(apiVersion, from).replace('.', '_');
            case "org/bukkit/map/MapCursor$Type" -> FieldRename.convertMapCursorTypeName(apiVersion, from);
            case "org/bukkit/inventory/ItemFlag" -> FieldRename.convertItemFlagName(apiVersion, from);
            default -> from;
        };
    }

    @RerouteStatic(value="java/lang/Enum")
    public static <T extends Enum<T>> T valueOf(Class<T> enumClass, String name, @InjectPluginVersion ApiVersion apiVersion) {
        return Enum.valueOf(enumClass, FieldRename.rename(apiVersion, enumClass.getName().replace('.', '/'), name));
    }

    @DoNotReroute
    public static String convertDisplaySlot(String from) {
        if (from.startsWith("SIDEBAR_") && !from.startsWith("SIDEBAR_TEAM_")) {
            return from.replace("SIDEBAR_", "SIDEBAR_TEAM_");
        }
        return from;
    }

    @DoNotReroute
    public static String convertPatternTypeName(ApiVersion version, String from) {
        return PATTERN_TYPE_DATA.getReplacement(version, from);
    }

    @RerouteMethodName(value="valueOf")
    @RerouteStatic(value="org/bukkit/block/banner/PatternType")
    public static PatternType valueOf_PatternType(String value, @InjectPluginVersion ApiVersion version) {
        return PatternType.valueOf((String)FieldRename.convertPatternTypeName(version, value));
    }

    @DoNotReroute
    public static String convertEnchantmentName(ApiVersion version, String from) {
        return ENCHANTMENT_DATA.getReplacement(version, from);
    }

    @RerouteMethodName(value="getByName")
    @RerouteStatic(value="org/bukkit/enchantments/Enchantment")
    public static Enchantment getByName_Enchantment(String name) {
        return Enchantment.getByName((String)FieldRename.convertEnchantmentName(ApiVersion.CURRENT, name));
    }

    @DoNotReroute
    public static String convertBiomeName(ApiVersion version, String from) {
        return BIOME_DATA.getReplacement(version, from);
    }

    @RerouteMethodName(value="valueOf")
    @RerouteStatic(value="org/bukkit/block/Biome")
    public static Biome valueOf_Biome(String name) {
        return Biome.valueOf((String)FieldRename.convertBiomeName(ApiVersion.CURRENT, name));
    }

    @DoNotReroute
    public static String convertEntityTypeName(ApiVersion version, String from) {
        return ENTITY_TYPE_DATA.getReplacement(version, from);
    }

    @RerouteMethodName(value="valueOf")
    @RerouteStatic(value="org/bukkit/entity/EntityType")
    public static EntityType valueOf_EntityType(String name) {
        return EntityType.valueOf((String)FieldRename.convertEntityTypeName(ApiVersion.CURRENT, name));
    }

    @RerouteMethodName(value="fromName")
    @RerouteStatic(value="org/bukkit/entity/EntityType")
    public static EntityType fromName_EntityType(String name) {
        return EntityType.fromName((String)FieldRename.convertEntityTypeName(ApiVersion.CURRENT, name));
    }

    @DoNotReroute
    public static String convertPotionEffectTypeName(ApiVersion version, String from) {
        return POTION_EFFECT_TYPE_DATA.getReplacement(version, from);
    }

    @RerouteMethodName(value="getByName")
    @RerouteStatic(value="org/bukkit/potion/PotionEffectType")
    public static PotionEffectType getByName_PotionEffectType(String name) {
        return PotionEffectType.getByName((String)FieldRename.convertPotionEffectTypeName(ApiVersion.CURRENT, name));
    }

    @DoNotReroute
    public static String convertPotionTypeName(ApiVersion version, String from) {
        return POTION_TYPE_DATA.getReplacement(version, from);
    }

    @RerouteMethodName(value="valueOf")
    @RerouteStatic(value="org/bukkit/potion/PotionType")
    public static PotionType valueOf_PotionType(String name) {
        return PotionType.valueOf((String)FieldRename.convertPotionTypeName(ApiVersion.CURRENT, name));
    }

    @DoNotReroute
    public static String convertMusicInstrumentName(ApiVersion version, String from) {
        return MUSIC_INSTRUMENT_DATA.getReplacement(version, from);
    }

    @DoNotReroute
    public static String convertParticleName(ApiVersion version, String from) {
        return PARTICLE_DATA.getReplacement(version, from);
    }

    @RerouteMethodName(value="valueOf")
    @RerouteStatic(value="org/bukkit/Particle")
    public static Particle valueOf_Particle(String name) {
        return Particle.valueOf((String)FieldRename.convertParticleName(ApiVersion.CURRENT, name));
    }

    @DoNotReroute
    public static String convertLootTablesName(ApiVersion version, String from) {
        return LOOT_TABLES_DATA.getReplacement(version, from);
    }

    @RerouteMethodName(value="valueOf")
    @RerouteStatic(value="org/bukkit/loot/LootTables")
    public static LootTables valueOf_LootTables(String name) {
        return LootTables.valueOf((String)FieldRename.convertLootTablesName(ApiVersion.CURRENT, name));
    }

    @DoNotReroute
    public static String convertAttributeName(ApiVersion version, String from) {
        return ATTRIBUTE_DATA.getReplacement(version, from);
    }

    @RerouteMethodName(value="valueOf")
    @RerouteStatic(value="org/bukkit/attribute/Attribute")
    public static Attribute valueOf_Attribute(String name) {
        return Attribute.valueOf((String)FieldRename.convertAttributeName(ApiVersion.CURRENT, name).replace('.', '_'));
    }

    @DoNotReroute
    public static String convertMapCursorTypeName(ApiVersion version, String from) {
        return MAP_CURSOR_TYPE_DATA.getReplacement(version, from);
    }

    @RerouteMethodName(value="valueOf")
    @RerouteStatic(value="org/bukkit/map/MapCursor$Type")
    public static MapCursor.Type valueOf_MapCursorType(String name, @InjectPluginVersion ApiVersion version) {
        return MapCursor.Type.valueOf((String)FieldRename.convertMapCursorTypeName(version, name));
    }

    @DoNotReroute
    public static String convertItemFlagName(ApiVersion version, String from) {
        return ITEM_FLAG_DATA.getReplacement(version, from);
    }

    @RerouteMethodName(value="valueOf")
    @RerouteStatic(value="org/bukkit/inventory/ItemFlag")
    public static ItemFlag valueOf_ItemFlag(String name) {
        return ItemFlag.valueOf((String)FieldRename.convertItemFlagName(ApiVersion.CURRENT, name));
    }
}


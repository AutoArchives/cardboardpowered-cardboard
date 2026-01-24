package org.bukkit.craftbukkit.util;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.FeatureFlag;
import org.bukkit.Fluid;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.RegionAccessor;
import org.bukkit.Statistic;
import org.bukkit.UnsafeValues;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.craftbukkit.CraftFeatureFlag;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftStatistic;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.potion.CraftPotionType;
import org.bukkit.damage.DamageEffect;
import org.bukkit.damage.DamageSource.Builder;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CreativeCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType.InternalPotionData;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.cardboardpowered.impl.CardboardModdedBlock;
import org.cardboardpowered.impl.CardboardModdedItem;
import org.cardboardpowered.impl.entity.CraftPlayer;
import org.cardboardpowered.util.GameVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.cardboardpowered.CardboardMod;
import org.cardboardpowered.interfaces.IMixinMaterial;
import org.cardboardpowered.interfaces.IMixinMinecraftServer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

import io.izzel.arclight.api.EnumHelper;
import io.izzel.arclight.api.Unsafe;
import io.papermc.paper.entity.EntitySerializationFlag;
import io.papermc.paper.inventory.ItemRarity;
import io.papermc.paper.inventory.tooltip.TooltipContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.PaperLifecycleEventManager;
import io.papermc.paper.registry.RegistryKey;
import me.isaiah.common.cmixin.IMixinItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.SharedConstants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.data.IMagicNumbers;
import org.bukkit.craftbukkit.damage.CraftDamageEffect;
import org.bukkit.craftbukkit.damage.CraftDamageSourceBuilder;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.cardboardpowered.BlockImplUtil;

@SuppressWarnings("deprecation")
public final class CraftMagicNumbers implements UnsafeValues, IMagicNumbers {
    
    public Material IgetMaterial(Block b) {return CraftMagicNumbers.getMaterial(b);}
    public Block IgetBlock(Material m) {return CraftMagicNumbers.getBlock(m);}

    public static final UnsafeValues INSTANCE = new CraftMagicNumbers();

    private CraftMagicNumbers() {}

    public static BlockState getBlock(MaterialData material) {
        return getBlock(material.getItemType(), material.getData());
    }

    public static BlockState getBlock(Material material, byte data) {
        return CraftLegacyMaterials.fromLegacyData(CraftLegacyMaterials.toLegacy(material), data);
    }

    public static MaterialData getMaterial(BlockState data) {
        return CraftLegacyMaterials.toLegacy(getMaterial(data.getBlock())).getNewData(toLegacyData(data));
    }

    public static Item getItem(Material material, short data) {
        if (material.isLegacy())
            return CraftLegacyMaterials.fromLegacyData(CraftLegacyMaterials.toLegacy(material), data);

        return getItem(material);
    }

    public static MaterialData getMaterialData(Item item) {
        return CraftLegacyMaterials.toLegacyData(getMaterial(item));
    }

    // ========================================================================
    private static final Map<Block, Material> BLOCK_MATERIAL = new HashMap<>();
    private static final Map<Item, Material> ITEM_MATERIAL = new HashMap<>();
    private static final Map<Material, Item> MATERIAL_ITEM = new HashMap<>();
    private static final Map<Material, Block> MATERIAL_BLOCK = new HashMap<>();
    private static final Map<net.minecraft.world.level.material.Fluid, org.bukkit.Fluid> FLUID_MATERIAL = new HashMap<>();
    private static final Map<Material, net.minecraft.world.level.material.Fluid> MATERIAL_FLUID = new HashMap<>();
    private static final Map<org.bukkit.entity.EntityType, net.minecraft.world.entity.EntityType<?>> ENTITY_TYPE_ENTITY_TYPES = new HashMap();
    private static final Map<net.minecraft.world.entity.EntityType<?>, org.bukkit.entity.EntityType> ENTITY_TYPES_ENTITY_TYPE = new HashMap();
    
    private static final TagParser<Tag> SNBT_REGISTRY_UNAWARE_PARSER;

    static {
        BlockImplUtil.setMN((IMagicNumbers)INSTANCE);
        
        for (org.bukkit.entity.EntityType type : org.bukkit.entity.EntityType.values()) {
            if (type == org.bukkit.entity.EntityType.UNKNOWN) continue;
            ENTITY_TYPE_ENTITY_TYPES.put(type, BuiltInRegistries.ENTITY_TYPE.getValue(CraftNamespacedKey.toMinecraft(type.getKey())));
            ENTITY_TYPES_ENTITY_TYPE.put(BuiltInRegistries.ENTITY_TYPE.getValue(CraftNamespacedKey.toMinecraft(type.getKey())), type);
        }
        
        for (Block block : BuiltInRegistries.BLOCK)
            BLOCK_MATERIAL.put(block, Material.getMaterial(BuiltInRegistries.BLOCK.getKey(block).getPath().toUpperCase(Locale.ROOT)));

        for (Item item : BuiltInRegistries.ITEM)
            ITEM_MATERIAL.put(item, Material.getMaterial(BuiltInRegistries.ITEM.getKey(item).getPath().toUpperCase(Locale.ROOT)));

        //for (net.minecraft.fluid.Fluid fluid : Registries.FLUID)
        //    FLUID_MATERIAL.put(fluid, org.bukkit.Registries.FLUID.get(CraftNamespacedKey.fromMinecraft(Registries.FLUID.getId(fluid))));

        for (net.minecraft.world.level.material.Fluid fluidType : BuiltInRegistries.FLUID) {
            if (BuiltInRegistries.FLUID.getKey(fluidType).getNamespace().equals(NamespacedKey.MINECRAFT)) {
                //Fluid fluid = org.bukkit.Registries.FLUID.get(CraftNamespacedKey.fromMinecraft(Registries.FLUID.getId(fluidType)));
               // if (fluid != null) {
               // 	FLUID_MATERIAL.put(fluidType, fluid);
               // }
            }
        }
        
        for (Material material : Material.values()) {
            if (material.isLegacy()) continue;

            Identifier key = key(material);
            BuiltInRegistries.ITEM.getOptional(key).ifPresent((item) -> MATERIAL_ITEM.put(material, item));
            BuiltInRegistries.BLOCK.getOptional(key).ifPresent((block) -> MATERIAL_BLOCK.put(material, block));
            BuiltInRegistries.FLUID.getOptional(key).ifPresent((fluid) -> MATERIAL_FLUID.put(material, fluid));
        }
        
        SNBT_REGISTRY_UNAWARE_PARSER = TagParser.create(NbtOps.INSTANCE);
    }

    public static final Map<String, Material> BY_NAME = Unsafe.getStatic(Material.class, "BY_NAME");
    private static final List<Class<?>> MAT_CTOR = ImmutableList.of(int.class);
    public static final HashMap<String, Material> MODDED_MATERIALS = new HashMap<>();

    public static final HashMap<Item, Material> MODDED_ITEM_MATERIAL = new HashMap<>();
    public static final HashMap<Material, Item> MODDED_MATERIAL_ITEM = new HashMap<>();

    @Deprecated
    public static void setupUnknownModdedMaterials() {
        for (Material material : Material.values()) {
            if (material.isLegacy()) continue;
            Identifier key = key(material);
            BuiltInRegistries.ITEM.getOptional(key).ifPresent((item) -> MATERIAL_ITEM.put(material, item));
            BuiltInRegistries.BLOCK.getOptional(key).ifPresent((block) -> MATERIAL_BLOCK.put(material, block));
            BuiltInRegistries.FLUID.getOptional(key).ifPresent((fluid) -> MATERIAL_FLUID.put(material, fluid));
        }
    }
    
    public static boolean has_mixin_interface(Material m) {
    	// Make sure mixin has applied
    	if ( (Object) m instanceof IMixinMaterial) {
    		return true;
    	}
    	return false;
    }

    public static void test() {
        // TODO: This needs to be kept updated when Spigot updates
        // It is the value of Material.values().length
    	CardboardMod.LOGGER.info("DEB: " + Material.values().length);
        int MATERIAL_LENGTH = 2104; // 1837; //1525;
        int i = MATERIAL_LENGTH - 1;

        List<String> names = new ArrayList<>();
        List<Material> list = new ArrayList<>();

        String lastMod = "";
        for (Block block : BuiltInRegistries.BLOCK) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            String name = standardize(id);
            String nam = id.getNamespace().toUpperCase(Locale.ROOT) + "_" + id.getPath().toUpperCase(Locale.ROOT);
            if (id.getNamespace().startsWith("minecraft")) {
            	boolean has = false;
            	try {
            		Material.valueOf(id.getPath().toUpperCase());
            		has = true;
            	} catch (IllegalArgumentException e) {
            		// Snapshot or API not updated.
            		has = false;
            		nam = id.getPath().toUpperCase(Locale.ROOT);
            	}
            	if (has) {
            		continue;
            	}
            }

            Material material = BY_NAME.get(name);
            if (null == material && !names.contains(name)) {
                material = EnumHelper.makeEnum(Material.class, name, i, MAT_CTOR, ImmutableList.of(i));
                if (!has_mixin_interface(material)) {
                    CardboardMod.LOGGER.warning("Material not instanceof IMixinMaterial");
                    return;
                }

                ((IMixinMaterial)(Object)material).setModdedData(new CardboardModdedBlock(id.toString()));
                MATERIAL_BLOCK.put(material, block);
                BY_NAME.put(name, material);
                list.add(material);
                MODDED_MATERIALS.put(name, material);
                
                if (!(lastMod.equalsIgnoreCase(id.namespace)))
                    CardboardMod.LOGGER.info("Registering modded blocks from mod '" + (lastMod = id.namespace) + "'..");
            }
            Material m = Material.getMaterial(nam);
            BLOCK_MATERIAL.put(block, m);
            MATERIAL_BLOCK.put(m, block);
        }

        for (Item item : BuiltInRegistries.ITEM) {
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            String name = standardize(id);
            String nam = id.getNamespace().toUpperCase(Locale.ROOT) + "_" + id.getPath().toUpperCase(Locale.ROOT);
            if (id.getNamespace().startsWith("minecraft")) {
            	boolean has = false;
            	try {
            		Material.valueOf(id.getPath().toUpperCase());
            		has = true;
            	} catch (IllegalArgumentException e) {
            		// Snapshot or API not updated.
            		nam = id.getPath().toUpperCase(Locale.ROOT);
            		has = false;
            	}
            	if (has) {
            		continue;
            	}
            }

            Material material = BY_NAME.get(name);
            if (null == material && !names.contains(name)) {
                material = EnumHelper.makeEnum(Material.class, name, i, MAT_CTOR, ImmutableList.of(i));
                if (!has_mixin_interface(material)) {
                    CardboardMod.LOGGER.warning("Material not instanceof IMixinMaterial");
                    return;
                }

                ((IMixinMaterial)(Object)material).setModdedData(new CardboardModdedItem(id.toString()));
                MATERIAL_ITEM.put(material, item);
                BY_NAME.put(name, material);
                list.add(material);
                MODDED_MATERIALS.put(name, material);

                if (!(lastMod.equalsIgnoreCase(id.namespace)))
                    CardboardMod.LOGGER.info("Registering modded items from mod '" + (lastMod = id.namespace) + "'..");
            }
            Material m = Material.getMaterial(nam);
            ITEM_MATERIAL.put(item, m);
            MATERIAL_ITEM.put(m, item);
        }

        //for (net.minecraft.fluid.Fluid fluid : Registries.FLUID)
        //    FLUID_MATERIAL.put(fluid, org.bukkit.Registries.FLUID.get(CraftNamespacedKey.fromMinecraft(Registries.FLUID.getId(fluid))));

        EnumHelper.addEnums(Material.class, list);

        for (Material material : list) {
            Identifier key = key(material);
            BuiltInRegistries.ITEM.getOptional(key).ifPresent((item) -> MATERIAL_ITEM.put(material, item));
            BuiltInRegistries.BLOCK.getOptional(key).ifPresent((block) -> MATERIAL_BLOCK.put(material, block));
            BuiltInRegistries.FLUID.getOptional(key).ifPresent((fluid) -> MATERIAL_FLUID.put(material, fluid));
        }
    }

    public static HashMap<String, Material> getModdedMaterials() {
        HashMap<String, Material> map = new HashMap<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            String name = standardize(id);
            if (id.getNamespace().startsWith("minecraft")) continue;

            map.put(name, Material.getMaterial(id.getNamespace().toUpperCase(Locale.ROOT) + "_" + id.getPath().toUpperCase(Locale.ROOT)));
        }

        for (Item item : BuiltInRegistries.ITEM) {
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            String name = standardize(id);
            if (id.getNamespace().startsWith("minecraft")) continue;

            map.put(name, Material.getMaterial(id.getNamespace().toUpperCase(Locale.ROOT) + "_" + id.getPath().toUpperCase(Locale.ROOT)));
        }
        return map;
    }

    public static String standardize(Identifier location) {
        Preconditions.checkNotNull(location, "location");
        return (location.getNamespace().equals(NamespacedKey.MINECRAFT) ? location.getPath() : location.toString())
            .replace(':', '_')
            .replaceAll("\\s+", "_")
            .replaceAll("\\W", "")
            .toUpperCase(Locale.ENGLISH);
    }

    public static String standardizeLower(Identifier location) {
        return (location.getNamespace().equals(NamespacedKey.MINECRAFT) ? location.getPath() : location.toString())
            .replace(':', '_')
            .replaceAll("\\s+", "_")
            .replaceAll("\\W", "")
            .toLowerCase(Locale.ENGLISH);
    }

    public static Material getMaterial(Block block) {
        Identifier id = BuiltInRegistries.BLOCK.getKey(block);
        Material m = BLOCK_MATERIAL.getOrDefault(block, Material.getMaterial(id.getNamespace().toUpperCase(Locale.ROOT) + "_" + id.getPath().toUpperCase(Locale.ROOT)));
        BLOCK_MATERIAL.put(block, m);
        MATERIAL_BLOCK.put(m, block);
        return m;
    }

    public static Material getMaterial(Item item) {
        return ITEM_MATERIAL.getOrDefault(item, Material.AIR);
    }

    public static Item getItem(Material material) {
        if (material != null && material.isLegacy()) material = CraftLegacyMaterials.fromLegacy(material);
        return MATERIAL_ITEM.getOrDefault(material, getModdedItem(material));
    }

    public static Block getBlock(Material material) {
        if (material != null && material.isLegacy()) material = CraftLegacyMaterials.fromLegacy(material);
        return MATERIAL_BLOCK.getOrDefault(material, getModdedBlock(material));
    }

    private static Item getModdedItem(Material mat) {
        if (!((Object)mat instanceof IMixinMaterial)) {
            // Dev env
            return null;
        }
        IMixinMaterial mm = (IMixinMaterial)(Object) mat;
        if (!mm.isModded()) return null;

        Identifier id = Identifier.parse(mm.getModdedData().getId());
        Item item = BuiltInRegistries.ITEM.getValue(id);
        MATERIAL_ITEM.put(mat, item);
        return item;
    }

    private static Block getModdedBlock(Material mat) {
        if (null == mat) return Blocks.STONE;
        if (!((Object)mat instanceof IMixinMaterial)) {
            // Dev env
        	return Blocks.STONE;
        }
        IMixinMaterial mm = (IMixinMaterial)(Object) mat;
        if (!mm.isModded()) return null;

        Identifier id = Identifier.parse(mm.getModdedData().getId());
        Block block = BuiltInRegistries.BLOCK.getValue(id);
        MATERIAL_BLOCK.put(mat, block);
        return block;
    }

    public static Identifier key(Material mat) {
        return CraftNamespacedKey.toMinecraft(mat.getKey());
    }
    // ========================================================================

    public static byte toLegacyData(BlockState data) {
        return CraftLegacyMaterials.toLegacyData(data);
    }

    @Override
    public Material toLegacy(Material material) {
        return CraftLegacyMaterials.toLegacy(material);
    }

    @Override
    public Material fromLegacy(Material material) {
        return CraftLegacyMaterials.fromLegacy(material);
    }

    @Override
    public Material fromLegacy(MaterialData material) {
        return CraftLegacyMaterials.fromLegacy(material);
    }

    @Override
    public Material fromLegacy(MaterialData material, boolean itemPriority) {
        return CraftLegacyMaterials.fromLegacy(material, itemPriority);
    }

    @Override
    public BlockData fromLegacy(Material material, byte data) {
        return CraftBlockData.fromData(getBlock(material, data));
    }

    @Override
    public Material getMaterial(String material, int version) {
        setupUnknownModdedMaterials();
        Preconditions.checkArgument(material != null, "material == null");
        Preconditions.checkArgument(version <= this.getDataVersion(), "Newer version! Server downgrades are not supported!");

        // Fastpath up to date materials
        if (version == this.getDataVersion()) return Material.getMaterial(material);

        Dynamic<Tag> name = new Dynamic<>(NbtOps.INSTANCE, StringTag.valueOf("minecraft:" + material.toLowerCase(Locale.ROOT)));
        Dynamic<Tag> converted = DataFixers.getDataFixer().update(References.ITEM_NAME, name, version, this.getDataVersion());

        if (name.equals(converted)) converted = DataFixers.getDataFixer().update(References.BLOCK_NAME, name, version, this.getDataVersion());
        return Material.matchMaterial(converted.asString(""));
    }

    @Deprecated
    public String getMappingsVersion() {
        return "60a2bb6bf2684dc61c56b90d7c41bddc";
    }

    @Override
    public int getDataVersion() {
        return GameVersion.create().world_version;
    }

    @Override
    public ItemStack modifyItemStack(ItemStack stack, String arguments) {
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

        /*try {
            nmsStack.setNbt((NbtCompound) StringNbtReader.parse(arguments));
        } catch (CommandSyntaxException ex) {
            BukkitLogger.getLogger(CraftMagicNumbers.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        ((IMixinItemStack)(Object)nmsStack).IC$modify_arguments(arguments); 

        stack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));
        return stack;
    }

    @Override
    public Advancement loadAdvancement(NamespacedKey key, String advancement) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean removeAdvancement(NamespacedKey key) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
    private static final List<String> SUPPORTED_API = Arrays.asList("1.13", "1.14", "1.15", "1.16", "1.17", "1.18", "1.19", "1.20", "1.21");

    //@Override
    public void checkSupported_old(PluginDescriptionFile pdf) throws InvalidPluginException {
        String minimumVersion = "1.12"; // TODO
        int minimumIndex = SUPPORTED_API.indexOf(minimumVersion);

        if (pdf.getAPIVersion() != null) {
            int pluginIndex = SUPPORTED_API.indexOf(pdf.getAPIVersion());
            if (pluginIndex == -1) {
            	throw new InvalidPluginException("Unsupported API version " + pdf.getAPIVersion());
            }

            if (pluginIndex < minimumIndex) {
                throw new InvalidPluginException("Plugin API version " + pdf.getAPIVersion() + " is lower than the minimum allowed version. Please update or replace it.");
            }
        } else {
            if (minimumIndex == -1) {
                CraftLegacyMaterials.init();
                Bukkit.getLogger().log(Level.WARNING, "Legacy plugin " + pdf.getFullName() + " does not specify an api-version.");
            } else throw new InvalidPluginException("Plugin API version " + pdf.getAPIVersion() + " is lower than the minimum allowed version. Please update or replace it.");
        }
    }
    */
    
    @Override
    public void checkSupported(PluginDescriptionFile pdf) throws InvalidPluginException {
        ApiVersion toCheck = ApiVersion.getOrCreateVersion(pdf.getAPIVersion());
        // ApiVersion minimumVersion = ApiVersion.getOrCreateVersion("1.12"); // TODO

        if (toCheck.isNewerThan(ApiVersion.CURRENT)) {
            // Newer than supported
            throw new InvalidPluginException("Unsupported API version " + pdf.getAPIVersion());
        }

        // if (toCheck.isOlderThan(minimumVersion)) {
        // }

        if (toCheck.isOlderThan(ApiVersion.FLATTENING)) {
            // TODO
        	// CraftLegacy.init();
        }

        if (toCheck == ApiVersion.NONE) {
            Bukkit.getLogger().log(Level.WARNING, "Legacy plugin " + pdf.getFullName() + " does not specify an api-version.");
        }
    }

    public static boolean isLegacy(PluginDescriptionFile pdf) {
        return pdf.getAPIVersion() == null;
    }
    
    private final Commodore commodore = new Commodore();

    public Commodore getCommodore() {
        return this.commodore;
    }
    
    @Override
    public byte[] processClass(PluginDescriptionFile pdf, String path, byte[] clazz) {
        /*
    	try {
            clazz = Commodore.convert(clazz, !isLegacy(pdf), pdf.getName());
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Fatal error trying to convert " + pdf.getFullName() + ":" + path, ex);
        }
        */

        try {
            clazz = this.commodore.convert(
            		clazz,
            		pdf.getName(),
            		ApiVersion.getOrCreateVersion(pdf.getAPIVersion()),
            		//((CraftServer) Bukkit.getServer()).activeCompatibilities
            		Collections.emptySet()
            		);
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Fatal error trying to convert " + pdf.getFullName() + ":" + path, ex);
        }
        
        return clazz;
    }

    // Paper start
    public boolean isSupportedApiVersion(String apiVersion) {
        return true;//apiVersion != null && SUPPORTED_API.contains(apiVersion);
    }
    // Paper end

    /**
     * This helper class represents the different NBT Tags.
     * <p>
     * These should match NBTBase#getTypeId
     */
    public static class NBT {
        public static final int TAG_END = 0;
        public static final int TAG_BYTE = 1;
        public static final int TAG_SHORT = 2;
        public static final int TAG_INT = 3;
        public static final int TAG_LONG = 4;
        public static final int TAG_FLOAT = 5;
        public static final int TAG_DOUBLE = 6;
        public static final int TAG_BYTE_ARRAY = 7;
        public static final int TAG_STRING = 8;
        public static final int TAG_LIST = 9;
        public static final int TAG_COMPOUND = 10;
        public static final int TAG_INT_ARRAY = 11;
        public static final int TAG_ANY_NUMBER = 99;
    }

    public static Fluid getFluid(net.minecraft.world.level.material.Fluid fluid) {
        return FLUID_MATERIAL.get(fluid);
    }

    public static net.minecraft.world.level.material.Fluid getFluid(Fluid fluid) {
        return MATERIAL_FLUID.get(fluid);
    }

    @Override
    public ItemStack deserializeItem(byte[] data) {
    	Preconditions.checkNotNull(data, "null cannot be deserialized");
        Preconditions.checkArgument(data.length > 0, "cannot deserialize nothing");

        CompoundTag compound = deserializeNbtFromBytes(data);
        return deserializeItem(compound);
    }

    // @Override
    public String getTimingsServerName() {
        return "Fabric";
    }

    //@Override
    public String getTranslationKey(Material arg0) {
        return arg0.name();
    }

    //@Override
    public String getTranslationKey(org.bukkit.block.Block arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTranslationKey(EntityType type) {
    	Preconditions.checkArgument(type.getName() != null, "Invalid name of EntityType %s for translation key", type);
        return net.minecraft.world.entity.EntityType.byString(type.getName()).map(net.minecraft.world.entity.EntityType::getDescriptionId).orElseThrow();
    }

    @Override
    public int nextEntityId() {
        // TODO Auto-generated method stub
        return 0;
    }

    // @Override
    public void reportTimings() {
    }

    @Override
    public byte[] serializeItem(ItemStack item) {
    	Preconditions.checkNotNull(item, "null cannot be serialized");
        Preconditions.checkArgument(!item.isEmpty(), "Empty itemstack cannot be serialized");
        return this.serializeNbtToBytes(
           (CompoundTag)net.minecraft.world.item.ItemStack.CODEC
              .encodeStart(CraftServer.server.registryAccess().createSerializationContext(NbtOps.INSTANCE), CraftItemStack.unwrap(item))
              .getOrThrow()
        );
    }

    // @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getItemAttributes(@NotNull Material arg0,
            @NotNull EquipmentSlot arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    // @Override
    public ItemRarity getItemRarity(Material arg0) {
        // TODO Auto-generated method stub
        return ItemRarity.COMMON;
    }

    // @Override
    public ItemRarity getItemStackRarity(ItemStack arg0) {
        // TODO Auto-generated method stub
        return ItemRarity.COMMON;
    }

    @Override
    public int getProtocolVersion() {
        // TODO Auto-generated method stub
        return SharedConstants.getProtocolVersion();
    }

    @Override
    public String getTranslationKey(ItemStack arg0) {
    	net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(arg0);
        return nmsItemStack.getItem().getDescriptionId();
    }

    @Override
    public boolean isValidRepairItemStack(@NotNull ItemStack arg0, @NotNull ItemStack arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    // Paper start
    @Override
    public net.kyori.adventure.text.flattener.ComponentFlattener componentFlattener() {
        return CardboardAdventure.FLATTENER;
    }

    @Override
    public net.kyori.adventure.text.serializer.gson.GsonComponentSerializer colorDownsamplingGsonComponentSerializer() {
        return CardboardAdventure.COLOR_DOWNSAMPLING_GSON;
    }

    @Override
    public net.kyori.adventure.text.serializer.gson.GsonComponentSerializer gsonComponentSerializer() {
        return CardboardAdventure.GSON;
    }

    @Override
    public net.kyori.adventure.text.serializer.plain.PlainComponentSerializer plainComponentSerializer() {
        return CardboardAdventure.PLAIN;
    }

    @Override
    public net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer legacyComponentSerializer() {
        return CardboardAdventure.LEGACY_SECTION_UXRC;
    }
    // Paper end

    @Override
    public Entity deserializeEntity(byte[] bs, World world, boolean bl) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public @NotNull Attributable getDefaultEntityAttributes(@NotNull NamespacedKey arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public boolean hasDefaultEntityAttributes(@NotNull NamespacedKey arg0) {
        // TODO Auto-generated method stub
        return false;
    }
    //@Override
    public boolean isCollidable(@NotNull Material arg0) {
        // TODO Auto-generated method stub
        return true;
    }
    @Override
    public byte[] serializeEntity(Entity entity) {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    // 1.18.2 api:
    
	@Override
	public CreativeCategory getCreativeCategory(Material arg0) {
		return CreativeCategory.BUILDING_BLOCKS;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(Material arg0, EquipmentSlot arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("resource")
	@Override
	public @NotNull String getMainLevelName() {
        return ((net.minecraft.server.dedicated.DedicatedServer) IMixinMinecraftServer.getServer()).getProperties().levelName;
	}

	@Override
	public PlainTextComponentSerializer plainTextSerializer() {
		// TODO Auto-generated method stub
		return null;
	}

	//@Override
	/*
	public <T extends Keyed> org.bukkit.@NotNull Registry<T> registryFor(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	*/
	
	// 1.19.2
	
	@Override
	public @NotNull NamespacedKey getBiomeKey(RegionAccessor arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Component resolveWithContext(Component arg0, CommandSender arg1, Entity arg2, boolean arg3)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setBiomeKey(RegionAccessor arg0, int arg1, int arg2, int arg3, NamespacedKey arg4) {
		// TODO Auto-generated method stub
		
	}
	
	// 1.19.4:

	@Override
    public String getBlockTranslationKey(Material material) {
        Block block = CraftMagicNumbers.getBlock(material);
        return block != null ? block.getDescriptionId() : null;
    }

	// @Override
	public FeatureFlag getFeatureFlag(@NotNull NamespacedKey key) {
        Preconditions.checkArgument(key != null, "key cannot be null");
        return CraftFeatureFlag.getFromNMS(key);
	}

	@Override
    public String getItemTranslationKey(Material material) {
        Item item = CraftMagicNumbers.getItem(material);
        return item != null ? item.getDescriptionId() : null;
    }
	
    public static net.minecraft.world.entity.EntityType<?> getEntityTypes(org.bukkit.entity.EntityType type) {
        return ENTITY_TYPE_ENTITY_TYPES.get(type);
    }

    public static org.bukkit.entity.EntityType getEntityType(net.minecraft.world.entity.EntityType<?> entityTypes) {
        return ENTITY_TYPES_ENTITY_TYPE.get(entityTypes);
    }

	@Override
	public String getStatisticCriteriaKey(@NotNull Statistic statistic) {
		if (statistic.getType() != Statistic.Type.UNTYPED) {
            return "minecraft.custom:minecraft." + statistic.getKey().getKey();
        }
        return CraftStatistic.getNMSStatistic(statistic).getName();
	}

	@Override
	public InternalPotionData getInternalPotionData(NamespacedKey key) {
		Potion potReg = CraftRegistry.getMinecraftRegistry(Registries.POTION).getOptional(CraftNamespacedKey.toMinecraft(key)).orElseThrow();
        return new CraftPotionType(key, potReg);
	}
	
	// 1.20.4 API:

	@Override
	public String getTranslationKey(Attribute attribute) {
        return CraftAttribute.bukkitToMinecraft(attribute).getDescriptionId();
	}

	// @Override
	public @Nullable DamageEffect getDamageEffect(String key) {
        return CraftDamageEffect.getById(key);
	}

	@Override
	public @NotNull Builder createDamageSourceBuilder(DamageType damageType) {
        return new CraftDamageSourceBuilder(damageType);
	}

	@Override
	public org.bukkit.Color getSpawnEggLayerColor(final EntityType entityType, final int layer) {
		final net.minecraft.world.entity.EntityType<?> nmsType = org.bukkit.craftbukkit.entity.CraftEntityType.bukkitToMinecraft(entityType);
		final net.minecraft.world.item.SpawnEggItem eggItem = net.minecraft.world.item.SpawnEggItem.byId(nmsType);
		if (eggItem != null) {
			throw new UnsupportedOperationException("Not yet implemented");
		}
		return eggItem == null ? null : org.bukkit.Color.fromRGB(1); // TODO
    }

	@Override
	public LifecycleEventManager<Plugin> createPluginLifecycleEventManager(JavaPlugin plugin,
			BooleanSupplier registrationCheck) {
		return new PaperLifecycleEventManager<JavaPlugin>(plugin, registrationCheck);
	}

	@Override
	public List<Component> computeTooltipLines(ItemStack itemStack, TooltipContext tooltipContext, Player player) {
        TooltipFlag.Default default_type = tooltipContext.isAdvanced() ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL;
        
        if (tooltipContext.isCreative()) {
        	default_type = default_type.asCreative();
        }
        
        List<net.minecraft.network.chat.Component> lines = CraftItemStack.asNMSCopy(itemStack).getTooltipLines(
        		Item.TooltipContext.of(
        			player == null ? CraftServer.server.registryAccess() :
        						((CraftPlayer)player).getHandle().level().registryAccess()
        		),
        		player == null ? null : ((CraftPlayer)player).getHandle(), default_type
        );
        return lines.stream().map(CardboardAdventure::asAdventure).toList();
    }
	
	@Override
	public String get(Class<?> aClass, String s) {
		if (aClass == Enchantment.class) {
            return FieldRename.convertEnchantmentName(ApiVersion.CURRENT, s);
        }
        return s;
	}

	@Override
	public <B extends Keyed> B get(RegistryKey<B> registry, NamespacedKey key) {
		return CraftRegistry.get(registry, key, ApiVersion.CURRENT);
	}

	// 1.21:

	@Override
	public ItemStack createEmptyStack() {
		return CraftItemStack.asCraftMirror(null);
	}

	@Override
	public @NotNull JsonObject serializeItemAsJson(@NotNull ItemStack itemStack) {
		JsonObject item;
        Preconditions.checkNotNull((Object)itemStack, (Object)"Cannot serialize empty ItemStack");
        Preconditions.checkArgument((!itemStack.isEmpty() ? 1 : 0) != 0, (Object)"Cannot serialize empty ItemStack");
        RegistryAccess.Frozen reg = CraftServer.server.registryAccess();
        RegistryOps ops = reg.createSerializationContext(JsonOps.INSTANCE);
        // TODO
        //NbtComponent.SERIALIZE_CUSTOM_AS_SNBT.set(true);
        try {
            item = ((JsonElement)net.minecraft.world.item.ItemStack.CODEC.encodeStart(ops, CraftItemStack.unwrap(itemStack)).getOrThrow()).getAsJsonObject();
        } finally {
            // TODO
        	// NbtComponent.SERIALIZE_CUSTOM_AS_SNBT.set(false);
        }
        item.addProperty("DataVersion", (Number)this.getDataVersion());
        return item;
	}

	@Override
	public ItemStack deserializeItemFromJson(JsonObject data) throws IllegalArgumentException {
		Preconditions.checkNotNull(data, "null cannot be deserialized");
		int dataVersion = data.get("DataVersion").getAsInt();
		int currentVersion = INSTANCE.getDataVersion();
		data = (JsonObject)CraftServer.server
				.fixerUpper
				.update(References.ITEM_STACK, new Dynamic<>(JsonOps.INSTANCE, data), dataVersion, currentVersion)
				.getValue();
		DynamicOps<JsonElement> ops = CraftServer.server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
		return CraftItemStack.asCraftMirror(
				(net.minecraft.world.item.ItemStack)net.minecraft.world.item.ItemStack.CODEC.parse(ops, data).getOrThrow(IllegalArgumentException::new)
				);
	}
	
	private byte[] serializeNbtToBytes(CompoundTag compound) {
        compound.putInt("DataVersion", getDataVersion());
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        try {
            net.minecraft.nbt.NbtIo.writeCompressed(
                compound,
                outputStream
            );
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return outputStream.toByteArray();
    }

    private CompoundTag deserializeNbtFromBytes(byte[] data) {
        CompoundTag compound;
        try {
            compound = net.minecraft.nbt.NbtIo.readCompressed(
                new java.io.ByteArrayInputStream(data), net.minecraft.nbt.NbtAccounter.unlimitedHeap()
            );
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        int dataVersion = compound.getIntOr("DataVersion", 0);
        Preconditions.checkArgument(dataVersion <= getDataVersion(), "Newer version! Server downgrades are not supported!");
        return compound;
    }
	
    /*
	 private Biome customBiome;
	    // @Override
	    public Biome getCustomBiome() {
	        if (this.customBiome == null) {
	            this.customBiome = new org.bukkit.craftbukkit.block.CraftBiome(NamespacedKey.minecraft("custom"), null);
	        }

	        return this.customBiome;
	    }
	    */


    @Override
    public byte[] serializeEntity(org.bukkit.entity.Entity entity, EntitySerializationFlag... serializationFlags) {
    	Preconditions.checkNotNull(entity, "null cannot be serialized");
    	Preconditions.checkArgument(entity instanceof CraftEntity, "Only CraftEntities can be serialized");

    	Set<EntitySerializationFlag> flags = Set.of(serializationFlags);
    	final boolean serializePassangers = flags.contains(EntitySerializationFlag.PASSENGERS);
    	final boolean forceSerialization = flags.contains(EntitySerializationFlag.FORCE);
    	final boolean allowPlayerSerialization = flags.contains(EntitySerializationFlag.PLAYER);
    	final boolean allowMiscSerialization = flags.contains(EntitySerializationFlag.MISC);
    	final boolean includeNonSaveable = allowPlayerSerialization || allowMiscSerialization;

    	net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
    	(serializePassangers ? nmsEntity.getSelfAndPassengers() : Stream.of(nmsEntity)).forEach(e -> {
    		// Ensure force flag is not needed
    		Preconditions.checkArgument(
    				(e.getBukkitEntity().isValid() && e.getBukkitEntity().isPersistent()) || forceSerialization,
    				"Cannot serialize invalid or non-persistent entity %s(%s) without the FORCE flag",
    				e.getType().toShortString(),
    				e.getStringUUID()
    				);

    		if (e instanceof net.minecraft.world.entity.player.Player) {
    			// Ensure player flag is not needed
    			Preconditions.checkArgument(
    					allowPlayerSerialization,
    					"Cannot serialize player(%s) without the PLAYER flag",
    					e.getStringUUID()
    					);
    		} else {
    			// Ensure player flag is not needed
    			Preconditions.checkArgument(
    					nmsEntity.getType().canSerialize() || allowMiscSerialization,
    					"Cannot serialize misc non-saveable entity %s(%s) without the MISC flag",
    					e.getType().toShortString(),
    					e.getStringUUID()
    					);
    		}
    	});

    	CompoundTag compound = new CompoundTag();
    	if (serializePassangers) {
    		// TODO
    		// if (!nmsEntity.saveAsPassenger(compound, true, includeNonSaveable, forceSerialization)) {
    		throw new IllegalArgumentException("Couldn't serialize entity");
    		// }
    	} else {
    		/*
	        	List<net.minecraft.entity.Entity> pass = new ArrayList<>(nmsEntity.getPassengerList());
	            nmsEntity.passengerList = com.google.common.collect.ImmutableList.of();
	            boolean serialized = nmsEntity.saveAsPassenger(compound, true, includeNonSaveable, forceSerialization);
	            nmsEntity.passengerList = com.google.common.collect.ImmutableList.copyOf(pass);
	            if (!serialized) {
	                throw new IllegalArgumentException("Couldn't serialize entity");
	            }
    		 */
    	}
    	return serializeNbtToBytes(compound);
    }

    @Override
    public @NotNull Entity deserializeEntity(byte @NotNull [] data, @NotNull World world, boolean preserveUUID,
    		boolean preservePassengers) {
    	// TODO Auto-generated method stub
    	return null;
    }
    @Override
    public @NotNull Map<String, Object> serializeStack(ItemStack itemStack) {
    	if (itemStack.isEmpty()) {
    		return Map.of("id", "minecraft:air", "DataVersion", this.getDataVersion(), "schema_version", 1);
    	}
    	CompoundTag tag = (CompoundTag)net.minecraft.world.item.ItemStack.CODEC.encodeStart(CraftRegistry.getMinecraftRegistry().createSerializationContext(NbtOps.INSTANCE), CraftItemStack.asNMSCopy(itemStack)).getOrThrow();
    	NbtUtils.addCurrentDataVersion(tag);
    	LinkedHashMap<String, Object> ret = new LinkedHashMap<String, Object>();
    	tag.asCompound().get().forEach((key, value) -> {
    		switch (key) {
    		case "id": {
    			ret.put("id", value.asString().get());
    			break;
    		}
    		case "count": {
    			ret.put("count", value.asInt().get());
    			break;
    		}
    		case "components": {
    			LinkedHashMap components = new LinkedHashMap();
    			value.asCompound().ifPresent(compoundTag -> compoundTag.forEach((componentKey, componentTag) -> {
    				String serializedComponent = componentTag.toString();
    				components.put(componentKey, serializedComponent);
    			}));
    			ret.put("components", components);
    			break;
    		}
    		case "DataVersion": {
    			ret.put("DataVersion", value.asInt().get());
    			break;
    		}
    		default: {
    			throw new IllegalStateException("Unexpected value: " + key);
    		}
    		}
    	});
    	ret.put("schema_version", 1);
    	return ret;
    }

    @NotNull
    public ItemStack deserializeStack(@NotNull Map<String, Object> args) {

    	int version = args.getOrDefault("schema_version", 1) instanceof Number val ? val.intValue() : -1;
    	
    	CompoundTag tag = new CompoundTag();
    	args.forEach((key, value) -> {
    		switch (key) {
    		case "id": {
    			tag.putString("id", (String)value);
    			break;
    		}
    		case "count": {
    			tag.putInt("count", ((Number)value).intValue());
    			break;
    		}
    		case "components": {
    			if (version == 1) {
    				HashMap<String, String> componentMap;
    				if (value instanceof Map) {
    					componentMap = (HashMap<String, String>)value;
    				} else if (value instanceof MemorySection) {
    					MemorySection memory = (MemorySection)value;
    					componentMap = new HashMap<String, String>();
    					for (String memoryKey : memory.getKeys(false)) {
    						componentMap.put(memoryKey, memory.getString(memoryKey));
    					}
    				} else {
    					throw new IllegalArgumentException("components must be a Map");
    				}
    				CompoundTag componentsTag = new CompoundTag();
    				componentMap.forEach((componentKey, componentString) -> {
    					Tag componentTag;
    					try {
    						componentTag = SNBT_REGISTRY_UNAWARE_PARSER.parseFully((String)componentString);
    					}
    					catch (CommandSyntaxException e2) {
    						throw new RuntimeException("Error parsing item stack data components", e2);
    					}
    					componentsTag.put((String)componentKey, componentTag);
    				});
    				tag.put("components", componentsTag);
    				break;
    			}
    			throw new IllegalStateException("Unexpected version: " + version);
    		}
    		case "DataVersion": {
    			tag.putInt("DataVersion", ((Number)value).intValue());
    			break;
    		}
    		case "==": 
    		case "schema_version": {
    			break;
    		}
    		default: {
    			throw new IllegalStateException("Unexpected value: " + key);
    		}
    		}
    	});
    	return this.deserializeItem(tag);
    }

    private ItemStack deserializeItem(CompoundTag compound) {
    	
    	int dataVersion = compound.getIntOr("DataVersion", 0);

    	// compound = PlatformHooks.get().convertNBT(TypeReferences.ITEM_STACK, Schemas.getFixer(), compound, dataVersion, this.getDataVersion());
    	compound = platformhooks$convertNBT(References.ITEM_STACK, DataFixers.getDataFixer(), compound, dataVersion, this.getDataVersion());


    	if (compound.getStringOr("id", "minecraft:air").equals("minecraft:air")) {
    		return CraftItemStack.asCraftMirror(net.minecraft.world.item.ItemStack.EMPTY);
    	}
    	return CraftItemStack.asCraftMirror((net.minecraft.world.item.ItemStack)net.minecraft.world.item.ItemStack.CODEC.parse(CraftRegistry.getMinecraftRegistry().createSerializationContext(NbtOps.INSTANCE), compound).getOrThrow());
    }

    /**
     * Cardboard
     * 
     * @see {@link ca.spottedleaf.moonrise.paper.PaperHooks}
     */
    public CompoundTag platformhooks$convertNBT(TypeReference type, DataFixer dataFixer, CompoundTag nbt, int fromVersion, int toVersion) {
    	return (CompoundTag)dataFixer.update(type, new Dynamic<>(NbtOps.INSTANCE, nbt), fromVersion, toVersion).getValue();
    }


}

package org.bukkit.craftbukkit.inventory;

// import static org.bukkit.craftbukkit.inventory.CraftMetaItem.ENCHANTMENTS_ID;
// import static org.bukkit.craftbukkit.inventory.CraftMetaItem.ENCHANTMENTS_LVL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang.Validate;
        import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
        import org.bukkit.craftbukkit.util.CraftLegacy;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.cardboardpowered.bridge.world.item.ItemStackBridge;

        import com.google.common.collect.ImmutableMap;
        import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
        import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

@DelegateDeserialization(ItemStack.class)
public final class CraftItemStack extends ItemStack {

	// Paper start - delegate api-ItemStack to CraftItemStack
    private static final java.lang.invoke.VarHandle API_ITEM_STACK_CRAFT_DELEGATE_FIELD;
    static {
        try {
            API_ITEM_STACK_CRAFT_DELEGATE_FIELD = java.lang.invoke.MethodHandles.privateLookupIn(
                ItemStack.class,
                java.lang.invoke.MethodHandles.lookup()
            ).findVarHandle(ItemStack.class, "craftDelegate", ItemStack.class);
        } catch (final IllegalAccessException | NoSuchFieldException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static CraftItemStack getCraftStack(final ItemStack bukkit) {
        if (bukkit instanceof final CraftItemStack craftItemStack) {
            return craftItemStack;
        } else {
            return  (CraftItemStack) API_ITEM_STACK_CRAFT_DELEGATE_FIELD.get(bukkit);
        }
    }

    @Override
    public int hashCode() {
        if (this.handle == null || this.handle.isEmpty()) {
            return net.minecraft.world.item.ItemStack.EMPTY.hashCode();
        } else {
            int hash = net.minecraft.world.item.ItemStack.hashItemAndComponents(this.handle);
            hash = hash * 31 + this.handle.getCount();
            return hash;
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof final org.bukkit.inventory.ItemStack bukkit)) return false;
        final CraftItemStack craftStack = getCraftStack(bukkit);
        if (this.handle == craftStack.handle) return true;
        else if (this.handle == null || craftStack.handle == null) return false;
        else if (this.handle.isEmpty() && craftStack.handle.isEmpty()) return true;
        else return net.minecraft.world.item.ItemStack.matches(this.handle, craftStack.handle);
    }
    // Paper end
    
 // Paper start - MC Utils

    public static net.minecraft.world.item.ItemStack getOrCloneOnMutation(ItemStack old, ItemStack newInstance) {
        return old == newInstance ? unwrap(old) : asNMSCopy(newInstance);
    }
    // Paper end - MC Utils
    
 // Paper start - override isEmpty to use vanilla's impl
    @Override
    public boolean isEmpty() {
        return handle == null || handle.isEmpty();
    }
    // Paper end - override isEmpty to use vanilla's impl
    
    public static net.minecraft.world.item.ItemStack asNMSCopy(ItemStack original) {
        // Paper start - re-implement after delegating all api ItemStack calls to CraftItemStack
        if (original == null || original.isEmpty()) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }
        final CraftItemStack stack = getCraftStack(original);
        return stack.handle == null ? net.minecraft.world.item.ItemStack.EMPTY : stack.handle.copy();
        // Paper end - re-implement after delegating all api ItemStack calls to CraftItemStack
    }
	
    /*
    public static net.minecraft.item.ItemStack asNMSCopy(ItemStack original) {

        if (original instanceof CraftItemStack) {
            CraftItemStack stack = (CraftItemStack) original;
            return stack.handle == null ? net.minecraft.item.ItemStack.EMPTY : stack.handle.copy();
        }
        if (original == null || original.getType() == Material.AIR)
            return net.minecraft.item.ItemStack.EMPTY;

        Item item = CraftMagicNumbers.getItem(original.getType(), original.getDurability());

        if (item == null)
            return net.minecraft.item.ItemStack.EMPTY;

        net.minecraft.item.ItemStack stack = new net.minecraft.item.ItemStack(item, original.getAmount());
        if (original.hasItemMeta())
            setItemMeta(stack, original.getItemMeta());

        return stack;
    }
    */
    
    public static List<net.minecraft.world.item.ItemStack> asNMSCopy(List<? extends ItemStack> originals) {
        ArrayList<net.minecraft.world.item.ItemStack> items = new ArrayList<net.minecraft.world.item.ItemStack>(originals.size());
        for (ItemStack itemStack : originals) {
            items.add(CraftItemStack.asNMSCopy(itemStack));
        }
        return items;
    }

    public static net.minecraft.world.item.ItemStack copyNMSStack(net.minecraft.world.item.ItemStack original, int amount) {
        net.minecraft.world.item.ItemStack stack = original.copy();
        stack.setCount(amount);
        return stack;
    }

    /**
     * Copies the NMS stack to return as a strictly-Bukkit stack
     */
    public static ItemStack asBukkitCopy(net.minecraft.world.item.ItemStack original) {
    	CraftItemStack stack = asCraftMirror(original.copy());
    	
        /*Material mat = CraftMagicNumbers.getMaterial(original.getItem());
        if (null == mat) {
            System.out.println("Unknown Bukkit Material (possible 1.17 material?): " + Registries.ITEM.getId(original.getItem()).getPath().toUpperCase());
        }

        ItemStack stack = new ItemStack(mat);
        if (hasItemMeta(original))
            stack.setItemMeta(getItemMeta(original));*/
        return stack;
    }

    public static CraftItemStack asCraftMirror(net.minecraft.world.item.ItemStack original) {
        return new CraftItemStack((original == null || original.isEmpty()) ? null : original);
    }

    public static CraftItemStack asCraftCopy(ItemStack original) {
        if (original instanceof CraftItemStack) {
            CraftItemStack stack = (CraftItemStack) original;
            return new CraftItemStack(stack.handle == null ? null : stack.handle.copy());
        }
        return new CraftItemStack(original);
    }

    public static CraftItemStack asNewCraftStack(Item item) {
        return asNewCraftStack(item, 1);
    }

    public static CraftItemStack asNewCraftStack(Item item, int amount) {
        return new CraftItemStack(CraftMagicNumbers.getMaterial(item), amount, (short) 0, null);
    }

    public net.minecraft.world.item.ItemStack handle;

    /**
     * Mirror
     */
    private CraftItemStack(net.minecraft.world.item.ItemStack item) {
        this.handle = item;
    }

    private CraftItemStack(ItemStack item) {
        this(item.getType(), item.getAmount(), item.getDurability(), item.hasItemMeta() ? item.getItemMeta() : null);
    }

    private CraftItemStack(Material type, int amount, short durability, ItemMeta itemMeta) {
        setType(type);
        setAmount(amount);
        setDurability(durability);
        setItemMeta(itemMeta);
    }

    @Override
    public MaterialData getData() {
        return handle != null ? CraftMagicNumbers.getMaterialData(handle.getItem()) : super.getData();
    }

    @Override
    public Material getType() {
        return handle != null ? CraftMagicNumbers.getMaterial(handle.getItem()) : Material.AIR;
    }

    @Override
    public void setType(Material type) {
        if (getType() == type) {
            return;
        } else if (type == Material.AIR) {
            handle = null;
        } else if (CraftMagicNumbers.getItem(type) == null) { // :(
            handle = null;
        } else if (handle == null) {
            handle = new net.minecraft.world.item.ItemStack(CraftMagicNumbers.getItem(type), 1);
        } else {
            handle.item = CraftMagicNumbers.getItem(type);
            if (hasItemMeta())
                setItemMeta(handle, getItemMeta(handle)); // This will create the appropriate item meta, which will contain all the data we intend to keep
        }
        setData((MaterialData) null);
    }

    @Override
    public int getAmount() {
        return handle != null ? handle.getCount() : 0;
    }

    @Override
    public void setAmount(int amount) {
        if (handle == null) return;

        handle.setCount(amount);
        if (amount == 0) handle = null;
    }

    @Override
    public void setDurability(final short durability) {
        // Ignore damage if item is null
        if (handle != null) handle.setDamageValue(durability);
    }

    @Override
    public short getDurability() {
        return handle != null ? (short) handle.getDamageValue() : -1;
    }

    @Override
    public int getMaxStackSize() {
        return (handle == null) ? Material.AIR.getMaxStackSize() : handle.getItem().getDefaultMaxStackSize();
    }

    @Override
    public void addUnsafeEnchantment(Enchantment ench, int level) {
    	
    	Validate.notNull(ench, "Cannot add null enchantment");
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.addEnchant(ench, level, true);
        this.setItemMeta(itemMeta);
    	
        /*Validate.notNull(ench, "Cannot add null enchantment");

        if (!makeTag(handle)) return;

        NbtList list = getEnchantmentList(handle);
        if (list == null) {
            list = new NbtList();
            handle.getNbt().put(ENCHANTMENTS.NBT, list);
        }
        int size = list.size();

        for (int i = 0; i < size; i++) {
            NbtCompound tag = (NbtCompound) list.get(i);
            String id = tag.getString(ENCHANTMENTS_ID.NBT);
            if (id.equals(ench.getKey().toString())) {
                tag.putShort(ENCHANTMENTS_LVL.NBT, (short) level);
                return;
            }
        }
        NbtCompound tag = new NbtCompound();
        tag.putString(ENCHANTMENTS_ID.NBT, ench.getKey().toString());
        tag.putShort(ENCHANTMENTS_LVL.NBT, (short) level);
        list.add(tag);*/
    }

    static boolean makeTag(net.minecraft.world.item.ItemStack item) {
        // if (item == null) return false;
        // if (item.getNbt() == null) item.setNbt(new NbtCompound());

        //return true;
    	
    	return item != null;
    }

    @Override
    public boolean containsEnchantment(Enchantment ench) {
        return getEnchantmentLevel(ench) > 0;
    }

    @Override
    public int getEnchantmentLevel(Enchantment ench) {
        
        return (handle == null) ? 0 : EnchantmentHelper.getItemEnchantmentLevel(CraftEnchantment.bukkitToMinecraftHolder(ench), handle);

    	
    	//return (handle == null) ? 0 : EnchantmentHelper.getLevel(CardboardEnchantment.getRaw(ench), handle);
    }
    
    @Override
    public int removeEnchantment(Enchantment ench) {
    	Validate.notNull(ench, "Cannot remove null enchantment");
        int level = this.getEnchantmentLevel(ench);
        if (level > 0) {
            ItemMeta itemMeta = this.getItemMeta();
            if (itemMeta == null) {
                return 0;
            }
            itemMeta.removeEnchant(ench);
            this.setItemMeta(itemMeta);
        }
        return level;
    }

    //@Override
    public int removeEnchantment_old(Enchantment ench) {
    	return removeEnchantment(ench);
        /*Validate.notNull(ench, "Cannot remove null enchantment");

        NbtList list = getEnchantmentList(handle), listCopy;
        if (list == null) return 0;

        int index = Integer.MIN_VALUE;
        int level = Integer.MIN_VALUE;
        int size = list.size();

        for (int i = 0; i < size; i++) {
            NbtCompound enchantment = (NbtCompound) list.get(i);
            String id = enchantment.getString(ENCHANTMENTS_ID.NBT);
            if (id.equals(ench.getKey().toString())) {
                index = i;
                level = 0xffff & enchantment.getShort(ENCHANTMENTS_LVL.NBT);
                break;
            }
        }

        if (index == Integer.MIN_VALUE) return 0;

        if (size == 1) {
            handle.getNbt().remove(ENCHANTMENTS.NBT);
            if (handle.getNbt().isEmpty())
                handle.setNbt(null);
            return level;
        }

        // This is workaround for not having an index removal
        listCopy = new NbtList();
        for (int i = 0; i < size; i++)
            if (i != index) listCopy.add(list.get(i));
        handle.getNbt().put(ENCHANTMENTS.NBT, listCopy);
        return level;*/
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return getEnchantments(handle);
    }
    
    static Map<Enchantment, Integer> getEnchantments(net.minecraft.world.item.ItemStack item) {
        ItemEnchantments list;
        ItemEnchantments itemEnchantmentsComponent = list = item != null && item.isEnchanted() ? item.get(DataComponents.ENCHANTMENTS) : null;
        if (list == null || list.size() == 0) {
            return ImmutableMap.of();
        }
        ImmutableMap.Builder result = ImmutableMap.builder();
        list.entrySet().forEach(entry -> {
            Holder id = (Holder)entry.getKey();
            int level = entry.getIntValue();
            // TODO
            // Enchantment enchant = CraftEnchantment.minecraftHolderToBukkit(id);
            Enchantment enchant = Enchantment.getByKey(CraftNamespacedKey.fromStringOrNull(id.getRegisteredName()));
            
            if (enchant != null) {
                result.put((Object)enchant, (Object)level);
            }
        });
        return result.build();
    }

    /*
    static Map<Enchantment, Integer> getEnchantments_old(net.minecraft.item.ItemStack item) {
        NbtList list = (item != null && item.hasEnchantments()) ? item.getEnchantments() : null;

        if (list == null || list.size() == 0)
            return ImmutableMap.of();

        ImmutableMap.Builder<Enchantment, Integer> result = ImmutableMap.builder();

        for (int i = 0; i < list.size(); i++) {
            String id = ((NbtCompound) list.get(i)).getString(ENCHANTMENTS_ID.NBT);
            int level = 0xffff & ((NbtCompound) list.get(i)).getShort(ENCHANTMENTS_LVL.NBT);

            Enchantment enchant = Enchantment.getByKey(CraftNamespacedKey.fromStringOrNull(id));
            if (enchant != null) result.put(enchant, level);
        }

        return result.build();
    }*/

    static ItemEnchantments getEnchantmentList(net.minecraft.world.item.ItemStack item) {
        return item != null && item.isEnchanted() ? item.get(DataComponents.ENCHANTMENTS) : null;
    }

    
    //static NbtList getEnchantmentList_old(net.minecraft.item.ItemStack item) {
    //    return (item != null && item.hasEnchantments()) ? item.getEnchantments() : null;
    //}

    @Override
    public CraftItemStack clone() {
        return new org.bukkit.craftbukkit.inventory.CraftItemStack(this.handle != null ? this.handle.copy() : null); // Paper
    	/*
    	CraftItemStack itemStack = (CraftItemStack) super.clone();
        if (this.handle != null)
            itemStack.handle = this.handle.copy();
        return itemStack;
        */
    }

    @Override
    public ItemMeta getItemMeta() {
        return getItemMeta(handle);
    }

    public static ItemMeta getItemMeta(net.minecraft.world.item.ItemStack item) {
        //if (!hasItemMeta(item))
       //     return CraftItemFactory.instance().getItemMeta(getType(item));

        return CraftItemStack.getItemMeta(item, CraftItemStack.getType(item));
    }
    

    public static ItemMeta getItemMeta(net.minecraft.world.item.ItemStack item, Material material) {
        return CraftItemStack.getItemMeta(item, material, null);
    }

    public static ItemMeta getItemMeta(net.minecraft.world.item.ItemStack item, Material material, Set<DataComponentType<?>> extraHandledDcts) {
        if (!CraftItemStack.hasItemMeta(item)) {
            return CraftItemFactory.instance().getItemMeta(material);
        }
        switch (material) {
            case WRITTEN_BOOK: {
                return new CraftMetaBookSigned(item.getComponentsPatch(), extraHandledDcts);
            }
            case WRITABLE_BOOK: {
                return new CraftMetaBook(item.getComponentsPatch(), extraHandledDcts);
            }
            case CREEPER_HEAD: 
            case CREEPER_WALL_HEAD: 
            case DRAGON_HEAD: 
            case DRAGON_WALL_HEAD: 
            case PIGLIN_HEAD: 
            case PIGLIN_WALL_HEAD: 
            case PLAYER_HEAD: 
            case PLAYER_WALL_HEAD: 
            case SKELETON_SKULL: 
            case SKELETON_WALL_SKULL: 
            case WITHER_SKELETON_SKULL: 
            case WITHER_SKELETON_WALL_SKULL: 
            case ZOMBIE_HEAD: 
            case ZOMBIE_WALL_HEAD: {
                return new CraftMetaSkull(item.getComponentsPatch(), extraHandledDcts);
            }
            case CHAINMAIL_HELMET: 
            case CHAINMAIL_CHESTPLATE: 
            case CHAINMAIL_LEGGINGS: 
            case CHAINMAIL_BOOTS: 
            case DIAMOND_HELMET: 
            case DIAMOND_CHESTPLATE: 
            case DIAMOND_LEGGINGS: 
            case DIAMOND_BOOTS: 
            case GOLDEN_HELMET: 
            case GOLDEN_CHESTPLATE: 
            case GOLDEN_LEGGINGS: 
            case GOLDEN_BOOTS: 
            case IRON_HELMET: 
            case IRON_CHESTPLATE: 
            case IRON_LEGGINGS: 
            case IRON_BOOTS: 
            case NETHERITE_HELMET: 
            case NETHERITE_CHESTPLATE: 
            case NETHERITE_LEGGINGS: 
            case NETHERITE_BOOTS: 
            case TURTLE_HELMET: {
                //return new CraftMetaArmor(item.getComponentChanges(), extraHandledDcts);
                return new CraftMetaItem(item.getComponentsPatch(), extraHandledDcts);
            }
            case LEATHER_HELMET: 
            case LEATHER_CHESTPLATE: 
            case LEATHER_LEGGINGS: 
            case LEATHER_BOOTS: 
            // case WOLF_ARMOR:
            {
                return new CraftMetaLeatherArmor(item.getComponentsPatch(), extraHandledDcts);
            }
            case LEATHER_HORSE_ARMOR: {
                return new CraftMetaLeatherArmor(item.getComponentsPatch(), extraHandledDcts);
            }
            case POTION: 
            case SPLASH_POTION: 
            case LINGERING_POTION: 
            case TIPPED_ARROW: {
                return new CraftMetaPotion(item.getComponentsPatch(), extraHandledDcts);
            }
            case FILLED_MAP: {
                return new CraftMetaMap(item.getComponentsPatch(), extraHandledDcts);
            }
            case FIREWORK_ROCKET: {
                return new CraftMetaFirework(item.getComponentsPatch(), extraHandledDcts);
            }
            case FIREWORK_STAR: {
                return new CraftMetaCharge(item.getComponentsPatch(), extraHandledDcts);
            }
            case ENCHANTED_BOOK: {
                return new CraftMetaEnchantedBook(item.getComponentsPatch(), extraHandledDcts);
            }
            case BLACK_BANNER: 
            case BLACK_WALL_BANNER: 
            case BLUE_BANNER: 
            case BLUE_WALL_BANNER: 
            case BROWN_BANNER: 
            case BROWN_WALL_BANNER: 
            case CYAN_BANNER: 
            case CYAN_WALL_BANNER: 
            case GRAY_BANNER: 
            case GRAY_WALL_BANNER: 
            case GREEN_BANNER: 
            case GREEN_WALL_BANNER: 
            case LIGHT_BLUE_BANNER: 
            case LIGHT_BLUE_WALL_BANNER: 
            case LIGHT_GRAY_BANNER: 
            case LIGHT_GRAY_WALL_BANNER: 
            case LIME_BANNER: 
            case LIME_WALL_BANNER: 
            case MAGENTA_BANNER: 
            case MAGENTA_WALL_BANNER: 
            case ORANGE_BANNER: 
            case ORANGE_WALL_BANNER: 
            case PINK_BANNER: 
            case PINK_WALL_BANNER: 
            case PURPLE_BANNER: 
            case PURPLE_WALL_BANNER: 
            case RED_BANNER: 
            case RED_WALL_BANNER: 
            case WHITE_BANNER: 
            case WHITE_WALL_BANNER: 
            case YELLOW_BANNER: 
            case YELLOW_WALL_BANNER: {
                return new CraftMetaBanner(item.getComponentsPatch(), extraHandledDcts);
            }
            // case ARMADILLO_SPAWN_EGG: 
            case ALLAY_SPAWN_EGG: 
            case AXOLOTL_SPAWN_EGG: 
            case BAT_SPAWN_EGG: 
            case BEE_SPAWN_EGG: 
            case BLAZE_SPAWN_EGG: 
            //case BOGGED_SPAWN_EGG: 
            //case BREEZE_SPAWN_EGG: 
            case CAT_SPAWN_EGG: 
            case CAMEL_SPAWN_EGG: 
            case CAVE_SPIDER_SPAWN_EGG: 
            case CHICKEN_SPAWN_EGG: 
            case COD_SPAWN_EGG: 
            case COW_SPAWN_EGG: 
            case CREEPER_SPAWN_EGG: 
            case DOLPHIN_SPAWN_EGG: 
            case DONKEY_SPAWN_EGG: 
            case DROWNED_SPAWN_EGG: 
            case ELDER_GUARDIAN_SPAWN_EGG: 
            case ENDER_DRAGON_SPAWN_EGG: 
            case ENDERMAN_SPAWN_EGG: 
            case ENDERMITE_SPAWN_EGG: 
            case EVOKER_SPAWN_EGG: 
            case FOX_SPAWN_EGG: 
            case FROG_SPAWN_EGG: 
            case GHAST_SPAWN_EGG: 
            case GLOW_SQUID_SPAWN_EGG: 
            case GOAT_SPAWN_EGG: 
            case GUARDIAN_SPAWN_EGG: 
            case HOGLIN_SPAWN_EGG: 
            case HORSE_SPAWN_EGG: 
            case HUSK_SPAWN_EGG: 
            case IRON_GOLEM_SPAWN_EGG: 
            case LLAMA_SPAWN_EGG: 
            case MAGMA_CUBE_SPAWN_EGG: 
            case MOOSHROOM_SPAWN_EGG: 
            case MULE_SPAWN_EGG: 
            case OCELOT_SPAWN_EGG: 
            case PANDA_SPAWN_EGG: 
            case PARROT_SPAWN_EGG: 
            case PHANTOM_SPAWN_EGG: 
            case PIGLIN_BRUTE_SPAWN_EGG: 
            case PIGLIN_SPAWN_EGG: 
            case PIG_SPAWN_EGG: 
            case PILLAGER_SPAWN_EGG: 
            case POLAR_BEAR_SPAWN_EGG: 
            case PUFFERFISH_SPAWN_EGG: 
            case RABBIT_SPAWN_EGG: 
            case RAVAGER_SPAWN_EGG: 
            case SALMON_SPAWN_EGG: 
            case SHEEP_SPAWN_EGG: 
            case SHULKER_SPAWN_EGG: 
            case SILVERFISH_SPAWN_EGG: 
            case SKELETON_HORSE_SPAWN_EGG: 
            case SKELETON_SPAWN_EGG: 
            case SLIME_SPAWN_EGG: 
            case SNIFFER_SPAWN_EGG: 
            case SNOW_GOLEM_SPAWN_EGG: 
            case SPIDER_SPAWN_EGG: 
            case SQUID_SPAWN_EGG: 
            case STRAY_SPAWN_EGG: 
            case STRIDER_SPAWN_EGG: 
            case TADPOLE_SPAWN_EGG: 
            case TRADER_LLAMA_SPAWN_EGG: 
            case TROPICAL_FISH_SPAWN_EGG: 
            case TURTLE_SPAWN_EGG: 
            case VEX_SPAWN_EGG: 
            case VILLAGER_SPAWN_EGG: 
            case VINDICATOR_SPAWN_EGG: 
            case WANDERING_TRADER_SPAWN_EGG: 
            case WARDEN_SPAWN_EGG: 
            case WITCH_SPAWN_EGG: 
            case WITHER_SKELETON_SPAWN_EGG: 
            case WITHER_SPAWN_EGG: 
            case WOLF_SPAWN_EGG: 
            case ZOGLIN_SPAWN_EGG: 
            case ZOMBIE_HORSE_SPAWN_EGG: 
            case ZOMBIE_SPAWN_EGG: 
            case ZOMBIE_VILLAGER_SPAWN_EGG: 
            case ZOMBIFIED_PIGLIN_SPAWN_EGG: {
                return new CraftMetaSpawnEgg(item.getComponentsPatch(), extraHandledDcts);
            }
            case ARMOR_STAND: {
                return new CraftMetaArmorStand(item.getComponentsPatch(), extraHandledDcts);
            }
            case KNOWLEDGE_BOOK: {
                return new CraftMetaKnowledgeBook(item.getComponentsPatch(), extraHandledDcts);
            }
            case FURNACE: 
            case CHEST: 
            case TRAPPED_CHEST: 
            case JUKEBOX: 
            case DISPENSER: 
            case DROPPER: 
            case ACACIA_HANGING_SIGN: 
            case ACACIA_SIGN: 
            case ACACIA_WALL_HANGING_SIGN: 
            case ACACIA_WALL_SIGN: 
            case BAMBOO_HANGING_SIGN: 
            case BAMBOO_SIGN: 
            case BAMBOO_WALL_HANGING_SIGN: 
            case BAMBOO_WALL_SIGN: 
            case BIRCH_HANGING_SIGN: 
            case BIRCH_SIGN: 
            case BIRCH_WALL_HANGING_SIGN: 
            case BIRCH_WALL_SIGN: 
            case CHERRY_HANGING_SIGN: 
            case CHERRY_SIGN: 
            case CHERRY_WALL_HANGING_SIGN: 
            case CHERRY_WALL_SIGN: 
            case CRIMSON_HANGING_SIGN: 
            case CRIMSON_SIGN: 
            case CRIMSON_WALL_HANGING_SIGN: 
            case CRIMSON_WALL_SIGN: 
            case DARK_OAK_HANGING_SIGN: 
            case DARK_OAK_SIGN: 
            case DARK_OAK_WALL_HANGING_SIGN: 
            case DARK_OAK_WALL_SIGN: 
            case JUNGLE_HANGING_SIGN: 
            case JUNGLE_SIGN: 
            case JUNGLE_WALL_HANGING_SIGN: 
            case JUNGLE_WALL_SIGN: 
            case MANGROVE_HANGING_SIGN: 
            case MANGROVE_SIGN: 
            case MANGROVE_WALL_HANGING_SIGN: 
            case MANGROVE_WALL_SIGN: 
            case OAK_HANGING_SIGN: 
            case OAK_SIGN: 
            case OAK_WALL_HANGING_SIGN: 
            case OAK_WALL_SIGN: 
            case SPRUCE_HANGING_SIGN: 
            case SPRUCE_SIGN: 
            case SPRUCE_WALL_HANGING_SIGN: 
            case SPRUCE_WALL_SIGN: 
            case WARPED_HANGING_SIGN: 
            case WARPED_SIGN: 
            case WARPED_WALL_HANGING_SIGN: 
            case WARPED_WALL_SIGN: 
            case SPAWNER: 
            case BREWING_STAND: 
            case ENCHANTING_TABLE: 
            case COMMAND_BLOCK: 
            case REPEATING_COMMAND_BLOCK: 
            case CHAIN_COMMAND_BLOCK: 
            case BEACON: 
            case DAYLIGHT_DETECTOR: 
            case HOPPER: 
            case COMPARATOR: 
            case STRUCTURE_BLOCK: 
            case SHULKER_BOX: 
            case WHITE_SHULKER_BOX: 
            case ORANGE_SHULKER_BOX: 
            case MAGENTA_SHULKER_BOX: 
            case LIGHT_BLUE_SHULKER_BOX: 
            case YELLOW_SHULKER_BOX: 
            case LIME_SHULKER_BOX: 
            case PINK_SHULKER_BOX: 
            case GRAY_SHULKER_BOX: 
            case LIGHT_GRAY_SHULKER_BOX: 
            case CYAN_SHULKER_BOX: 
            case PURPLE_SHULKER_BOX: 
            case BLUE_SHULKER_BOX: 
            case BROWN_SHULKER_BOX: 
            case GREEN_SHULKER_BOX: 
            case RED_SHULKER_BOX: 
            case BLACK_SHULKER_BOX: 
            case ENDER_CHEST: 
            case BARREL: 
            case BELL: 
            case BLAST_FURNACE: 
            case CAMPFIRE: 
            case SOUL_CAMPFIRE: 
            case JIGSAW: 
            case LECTERN: 
            case SMOKER: 
            case BEEHIVE: 
            case BEE_NEST: 
            case SCULK_CATALYST: 
            case SCULK_SHRIEKER: 
            case SCULK_SENSOR: 
            // case CALIBRATED_SCULK_SENSOR: 
            case CHISELED_BOOKSHELF: 
            case DECORATED_POT: 
            case SUSPICIOUS_SAND: 
            // case SUSPICIOUS_GRAVEL: 
            // case CRAFTER: 
            // case TRIAL_SPAWNER: 
            // case VAULT:
            {
                return new CraftMetaBlockState(item.getComponentsPatch(), CraftItemType.minecraftToBukkit(item.getItem()), extraHandledDcts);
            }
            case SHIELD: {
                return new CraftMetaShield(item.getComponentsPatch(), extraHandledDcts);
            }
            case TROPICAL_FISH_BUCKET: {
            	return new CraftMetaItem(item.getComponentsPatch(), extraHandledDcts);
            	//return new CraftMetaTropicalFishBucket(item.getComponentChanges(), extraHandledDcts);
            }
            case AXOLOTL_BUCKET: {
            	return new CraftMetaItem(item.getComponentsPatch(), extraHandledDcts);
            	// return new CraftMetaAxolotlBucket(item.getComponentChanges(), extraHandledDcts);
            }
            case CROSSBOW: {
                return new CraftMetaCrossbow(item.getComponentsPatch(), extraHandledDcts);
            }
            case SUSPICIOUS_STEW: {
                return new CraftMetaSuspiciousStew(item.getComponentsPatch(), extraHandledDcts);
            }
            case COD_BUCKET: 
            case PUFFERFISH_BUCKET: 
            case SALMON_BUCKET: 
            case TADPOLE_BUCKET: 
            case ITEM_FRAME: 
            case GLOW_ITEM_FRAME: 
            case PAINTING: {
                return new CraftMetaEntityTag(item.getComponentsPatch(), extraHandledDcts);
            }
			/*
            case COMPASS: {
                return new CraftMetaCompass(item.getComponentChanges(), extraHandledDcts);
            }
            case BUNDLE: {
                return new CraftMetaBundle(item.getComponentChanges(), extraHandledDcts);
            }
            case GOAT_HORN: {
                return new CraftMetaMusicInstrument(item.getComponentChanges(), extraHandledDcts);
            }
            case OMINOUS_BOTTLE: {
                return new CraftMetaOminousBottle(item.getComponentChanges(), extraHandledDcts);
            }*/
        }
        return new CraftMetaItem(item.getComponentsPatch(), extraHandledDcts);
    }

    static Material getType(net.minecraft.world.item.ItemStack item) {
        if (null != item) {
            boolean isModded = (null == CraftMagicNumbers.getMaterial(item.getItem()));
            if (isModded) {
                Identifier id = BuiltInRegistries.ITEM.getKey(item.item);
                String name = CraftMagicNumbers.standardize(id);
                Material material = Material.getMaterial(name);                    
                return material;
            }
        }
        return item == null ? Material.AIR : CraftMagicNumbers.getMaterial(item.getItem());
    }

    @Override
    public boolean setItemMeta(ItemMeta itemMeta) {
        return setItemMeta(handle, itemMeta);
    }
    
    public static boolean setItemMeta(final net.minecraft.world.item.ItemStack item, ItemMeta itemMeta) {
        Item newItem;
        if (item == null) {
            return false;
        }
        if (CraftItemFactory.instance().equals(itemMeta, null)) {
        	((ItemStackBridge)item).cardboard$restore_patch(DataComponentPatch.EMPTY);
            return true;
        }
        if (!CraftItemFactory.instance().isApplicable(itemMeta, CraftItemStack.getType(item))) {
            return false;
        }
        itemMeta = CraftItemFactory.instance().asMetaFor(itemMeta, CraftItemStack.getType(item));
        if (itemMeta == null) {
            return true;
        }
        Item oldItem = item.getItem();
        if (oldItem != (newItem = CraftItemType.bukkitToMinecraft(CraftItemFactory.instance().updateMaterial(itemMeta, CraftItemType.minecraftToBukkit(oldItem))))) {
        	((ItemStackBridge)item).cb$setItem(newItem);
        }
        if (!((CraftMetaItem)itemMeta).isEmpty()) {
            CraftMetaItem.Applicator tag = new CraftMetaItem.Applicator(){

                @Override
                void skullCallback(ResolvableProfile gameProfile) {
                    item.set(DataComponents.PROFILE, gameProfile);
                }
            };
            ((CraftMetaItem)itemMeta).applyToItem(tag);
            // item.restorePatch(tag.build());
            ((ItemStackBridge)item).cardboard$restore_patch(tag.build());
        }
        if (item.getItem() != null && item.getMaxDamage() > 0) {
            item.setDamageValue(item.getDamageValue());
        }
        return true;
    }
    
    public static void applyMetaToItem(net.minecraft.world.item.ItemStack itemStack, ItemMeta itemMeta) {
        CraftMetaItem.Applicator tag = new CraftMetaItem.Applicator() {
           @Override
           void skullCallback(ResolvableProfile profile) {
              itemStack.set(DataComponents.PROFILE, profile);
           }
        };
        ((CraftMetaItem)itemMeta).applyToItem(tag);
        itemStack.applyComponents(tag.build());
     }

    /*public static boolean setItemMeta_old(net.minecraft.item.ItemStack item, ItemMeta itemMeta) {
        if (item == null) return false;

        if (CraftItemFactory.instance().equals(itemMeta, null)) {
            item.setNbt(null);
            return true;
        }
        if (!CraftItemFactory.instance().isApplicable(itemMeta, getType(item)))
            return false;

        itemMeta = CraftItemFactory.instance().asMetaFor(itemMeta, getType(item));
        if (itemMeta == null) return true;

        Item oldItem = item.getItem();
        Item newItem = CraftMagicNumbers.getItem(CraftItemFactory.instance().updateMaterial(itemMeta, CraftMagicNumbers.getMaterial(oldItem)));
        if (oldItem != newItem)
            item.item = newItem;

        NbtCompound tag = new NbtCompound();
        item.setNbt(tag);

        ((CraftMetaItem) itemMeta).applyToItem(tag);
        convertStack_BF(item, ((CraftMetaItem) itemMeta).getVersion());

        if (item.getItem() != null && item.getItem().isDamageable()) // SpigotCraft#463 
            item.setDamage(item.getDamage());

        return true;
    }*/

    /*@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
    public static void convertStack_BF(net.minecraft.item.ItemStack item, int version) {
        if (0 < version && version < CraftMagicNumbers.INSTANCE.getDataVersion()) {
            NbtCompound savedStack = new NbtCompound();

            item.writeNbt(savedStack);
            savedStack = (NbtCompound) CraftServer.server.getDataFixer().update(TypeReferences.ITEM_STACK, new Dynamic(NbtOps.INSTANCE, savedStack), version, CraftMagicNumbers.INSTANCE.getDataVersion()).getValue();
            item.setNbt(savedStack);
        }
    }*/

    /*@Override
    public boolean isSimilar(ItemStack stack) {
        if (stack == null) return false;
        if (stack == this) return true;

        if (!(stack instanceof CraftItemStack))
            return stack.getClass() == ItemStack.class && stack.isSimilar(this);

        CraftItemStack that = (CraftItemStack) stack;
        if (handle == that.handle) return true;
        if (handle == null || that.handle == null) return false;

        Material comparisonType = CraftLegacy.fromLegacy(that.getType()); // This may be called from legacy item stacks, try to get the right material
        if (!(comparisonType == getType() && getDurability() == that.getDurability()))
            return false;

        return hasItemMeta() ? that.hasItemMeta() && handle.getNbt().equals(that.handle.getNbt()) : !that.hasItemMeta();
    }*/
    
    @Override
    public boolean isSimilar(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack == this) {
            return true;
        }
        if (!(stack instanceof CraftItemStack)) {
            return stack.getClass() == ItemStack.class && stack.isSimilar((ItemStack)this);
        }
        CraftItemStack that = getCraftStack(stack);// (CraftItemStack)stack;
        if (this.handle == that.handle) {
            return true;
        }
        if (this.handle == null || that.handle == null) {
            return false;
        }
        Material comparisonType = CraftLegacy.fromLegacy(that.getType());
        if (comparisonType != this.getType() || this.getDurability() != that.getDurability()) {
            return false;
        }
        return this.hasItemMeta() ? that.hasItemMeta() && this.handle.getComponents().equals(that.handle.getComponents()) : !that.hasItemMeta();
    }

    @Override
    public boolean hasItemMeta() {
        return hasItemMeta(handle) && !CraftItemFactory.instance().equals(getItemMeta(), null);
    }
    
    static boolean hasItemMeta(net.minecraft.world.item.ItemStack item) {
        return item != null && !item.getComponentsPatch().isEmpty();
    }

    /*
    public static net.minecraft.item.ItemStack unwrap(ItemStack bukkit) {
        if (bukkit instanceof CraftItemStack) {
            CraftItemStack craftItemStack = (CraftItemStack)bukkit;
            return craftItemStack.handle != null ? craftItemStack.handle : net.minecraft.item.ItemStack.EMPTY;
        }
        return CraftItemStack.asNMSCopy(bukkit);
    }
    */
    
    public static net.minecraft.world.item.ItemStack unwrap(ItemStack bukkit) {
        // Paper start - re-implement after delegating all api ItemStack calls to CraftItemStack
        final CraftItemStack craftItemStack = getCraftStack(bukkit);
        return craftItemStack.handle == null ? net.minecraft.world.item.ItemStack.EMPTY : craftItemStack.handle;
        // Paper end - re-implement after delegating all api ItemStack calls to CraftItemStack
    }

    public static ItemPredicate asCriterionConditionItem(ItemStack original) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(original);
        DataComponentExactPredicate predicate = DataComponentExactPredicate.allOf(PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, nms.getComponentsPatch()));
        return new ItemPredicate(Optional.of(HolderSet.direct(nms.getItemHolder())), MinMaxBounds.Ints.ANY, new DataComponentMatchers(predicate, Collections.emptyMap()));
    }

    /*static boolean hasItemMeta(net.minecraft.item.ItemStack item) {
        return !(item == null || item.getNbt() == null || item.getNbt().isEmpty());
    }*/
    
 // Paper start - data component API
    @Override
    public <T> T getData(final io.papermc.paper.datacomponent.DataComponentType.Valued<T> type) {
        if (this.isEmpty()) {
            return null;
        }
        return io.papermc.paper.datacomponent.PaperDataComponentType.convertDataComponentValue(this.handle.getComponents(), (io.papermc.paper.datacomponent.PaperDataComponentType.ValuedImpl<T, ?>) type);
    }

    @Override
    public boolean hasData(final io.papermc.paper.datacomponent.DataComponentType type) {
        if (this.isEmpty()) {
            return false;
        }
        return this.handle.has(io.papermc.paper.datacomponent.PaperDataComponentType.bukkitToMinecraft(type));
    }

    @Override
    public java.util.Set<io.papermc.paper.datacomponent.DataComponentType> getDataTypes() {
        if (this.isEmpty()) {
            return java.util.Collections.emptySet();
        }
        return io.papermc.paper.datacomponent.PaperDataComponentType.minecraftToBukkit(this.handle.getComponents().keySet());
    }

    @Override
    public <T> void setData(final io.papermc.paper.datacomponent.DataComponentType.Valued<T> type, final T value) {
        // Preconditions.checkArgument(value != null, "value cannot be null");
        if (this.isEmpty()) {
            return;
        }
        this.setDataInternal((io.papermc.paper.datacomponent.PaperDataComponentType.ValuedImpl<T, ?>) type, value);
    }

    @Override
    public void setData(final io.papermc.paper.datacomponent.DataComponentType.NonValued type) {
        if (this.isEmpty()) {
            return;
        }
        this.setDataInternal((io.papermc.paper.datacomponent.PaperDataComponentType.NonValuedImpl<?, ?>) type, null);
    }

    private <A, V> void setDataInternal(final io.papermc.paper.datacomponent.PaperDataComponentType<A, V> type, final A value) {
        this.handle.set(type.getHandle(), type.getAdapter().toVanilla(value, type.getHolder()));
    }

    @Override
    public void unsetData(final io.papermc.paper.datacomponent.DataComponentType type) {
        if (this.isEmpty()) {
            return;
        }
        this.handle.remove(io.papermc.paper.datacomponent.PaperDataComponentType.bukkitToMinecraft(type));
    }

    @Override
    public void resetData(final io.papermc.paper.datacomponent.DataComponentType type) {
        if (this.isEmpty()) {
            return;
        }
        this.resetData((io.papermc.paper.datacomponent.PaperDataComponentType<?, ?>) type);
    }

    private <M> void resetData(final io.papermc.paper.datacomponent.PaperDataComponentType<?, M> type) {
        final net.minecraft.core.component.DataComponentType<M> nms = io.papermc.paper.datacomponent.PaperDataComponentType.bukkitToMinecraft(type);
        final M nmsValue = this.handle.getItem().components().get(nms);
        // if nmsValue is null, it will clear any set patch
        // if nmsValue is not null, it will still clear any set patch because it will equal the default value
        this.handle.set(nms, nmsValue);
    }

    @Override
    public boolean isDataOverridden(final io.papermc.paper.datacomponent.DataComponentType type) {
        if (this.isEmpty()) {
            return false;
        }
        final net.minecraft.core.component.DataComponentType<?> nms = io.papermc.paper.datacomponent.PaperDataComponentType.bukkitToMinecraft(type);
        // maybe a more efficient way is to expose the "patch" map in PatchedDataComponentMap and just check if the type exists as a key
        return !java.util.Objects.equals(this.handle.get(nms), this.handle.getPrototype().get(nms));
    }

    @Override
    public boolean matchesWithoutData(final ItemStack item, final java.util.Set<io.papermc.paper.datacomponent.DataComponentType> exclude, final boolean ignoreCount) {
        // Extracted from base equals
        final CraftItemStack craftStack = getCraftStack(item);
        if (this.handle == craftStack.handle) return true;
        if (this.handle == null || craftStack.handle == null) return false;
        if (this.handle.isEmpty() && craftStack.handle.isEmpty()) return true;

        net.minecraft.world.item.ItemStack left = this.handle;
        net.minecraft.world.item.ItemStack right = craftStack.handle;
        if (!ignoreCount && left.getCount() != right.getCount()) {
            return false;
        }
        if (!left.is(right.getItem())) {
            return false;
        }

        // It can be assumed that the prototype is equal since the type is the same. This way all we need to check is the patch

        // Fast path when excluded types is empty
        if (exclude.isEmpty()) {
            return left.getComponentsPatch().equals(right.getComponentsPatch());
        }

        // Collect all the NMS types into a set
        java.util.Set<net.minecraft.core.component.DataComponentType<?>> skippingTypes = new java.util.HashSet<>(exclude.size());
        for (io.papermc.paper.datacomponent.DataComponentType api : exclude) {
            skippingTypes.add(io.papermc.paper.datacomponent.PaperDataComponentType.bukkitToMinecraft(api));
        }

        // Check the patch by first stripping excluded types and then compare the trimmed patches
        return left.getComponentsPatch().forget(skippingTypes::contains).equals(right.getComponentsPatch().forget(skippingTypes::contains));
    }

    // Paper end - data component API

}
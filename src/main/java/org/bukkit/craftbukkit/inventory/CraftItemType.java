package org.bukkit.craftbukkit.inventory;

import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.attribute.CraftAttributeInstance;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.cardboardpowered.impl.world.CraftWorld;
import org.jetbrains.annotations.NotNull;
import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.cardboardpowered.interfaces.IMixinMinecraftServer;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentType.Valued;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.entity.FuelValues;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;

import org.bukkit.craftbukkit.block.CraftBlockType;

import org.bukkit.inventory.CreativeCategory;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

/*
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.attribute.CraftAttributeInstance;
*/

import org.bukkit.craftbukkit.inventory.CraftMetaEntityTag;



public class CraftItemType<M extends ItemMeta> implements ItemType.Typed<M>, Handleable<Item> {

	/*
    public static Material minecraftToBukkit(Item item) {
        return CraftMagicNumbers.getMaterial(item);
    }

    public static Item bukkitToMinecraft(Material material) {
        return CraftMagicNumbers.getItem(material);
    }

    public static Item bukkitToMinecraftNew(ItemType bukkit) {
        return (Item)CraftRegistry.bukkitToMinecraft(bukkit);
    }

	public static @NotNull ItemType minecraftToBukkitNew(Item item) {
		// TODO Auto-generated method stub
		return null;
	}
	*/
	
	private final NamespacedKey key;
    private final Item item;
    // private final Class<M> itemMetaClass;
    private final Supplier<CraftItemMetas.ItemMetaData<M>> itemMetaData;
    

    public static Material minecraftToBukkit(Item item) {
        return CraftMagicNumbers.getMaterial(item);
    }

    public static Item bukkitToMinecraft(Material material) {
        return CraftMagicNumbers.getItem(material);
    }

    public static ItemType minecraftToBukkitNew(Item minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, Registries.ITEM);
    }

    public static Item bukkitToMinecraftNew(ItemType bukkit) {
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public CraftItemType(NamespacedKey key, Item item) {
        this.key = key;
        this.item = item;
        // this.itemMetaClass = this.getItemMetaClass(item);
        this.itemMetaData = Suppliers.memoize(() -> CraftItemMetas.getItemMetaData(this));
    }

    /*
    private Class<M> getItemMetaClass(Item item) {
        ItemMeta meta = new ItemStack(this.asMaterial()).getItemMeta();
        if (meta != null && CraftMetaEntityTag.class != meta.getClass()) {
            return (Class<M>) meta.getClass().getInterfaces()[0];
        }
        return (Class<M>) ItemMeta.class;
    }
    */

    @NotNull
    public ItemType.Typed<ItemMeta> typed() {
        return this.typed(ItemMeta.class);
    }

    @NotNull
    public <Other extends ItemMeta> ItemType.Typed<Other> typed(@NotNull Class<Other> itemMetaType) {
    	if (itemMetaType.isAssignableFrom(this.itemMetaData.get().metaClass())) {
            return (ItemType.Typed<Other>) this;
        }
        throw new IllegalArgumentException("Cannot type item type " + this.key.toString() + " to meta type " + itemMetaType.getSimpleName());
    }

    @NotNull
    public ItemStack createItemStack() {
        return this.createItemStack(1, null);
    }

    @NotNull
    public ItemStack createItemStack(int amount) {
        return this.createItemStack(amount, null);
    }

    @NotNull
    public ItemStack createItemStack(Consumer<? super M> metaConfigurator) {
        return this.createItemStack(1, metaConfigurator);
    }

    public ItemStack createItemStack(int amount, @Nullable Consumer<? super M> metaConfigurator) {
        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(this.item, amount);
        CraftItemStack mirror = CraftItemStack.asCraftMirror(stack);
        if (metaConfigurator != null) {
            mirror.editMeta(this.getItemMetaClass(), metaConfigurator);
        }
        return mirror;
    }

    /*
    @NotNull
    public ItemStack createItemStack(int amount, @Nullable Consumer<? super M> metaConfigurator) {
        ItemStack itemStack = new ItemStack(this.asMaterial(), amount);
        if (metaConfigurator != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            metaConfigurator.accept((M) itemMeta);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
    */

    @Override
    public Item getHandle() {
        return this.item;
    }

    public boolean hasBlockType() {
        return this.item instanceof BlockItem;
    }

    @NotNull
    public BlockType getBlockType() {
        Item item = this.item;
        if (!(item instanceof BlockItem)) {
            throw new IllegalStateException("The item type " + String.valueOf(this.getKey()) + " has no corresponding block type");
        }
        BlockItem block = (BlockItem)item;
        return CraftBlockType.minecraftToBukkitNew(block.getBlock());
    }

    public Class<M> getItemMetaClass() {
        if (this == ItemType.AIR) {
            throw new UnsupportedOperationException("Air does not have ItemMeta");
        }
        return this.itemMetaData.get().metaClass();
    }
    
    /*
    public Class<M> getItemMetaClass_old() {
        if (this == ItemType.AIR) {
            throw new UnsupportedOperationException("Air does not have ItemMeta");
        }
        return this.itemMetaClass;
    }
    */

    public int getMaxStackSize() {
        if (this == AIR) {
            return 0;
        }
        return this.item.components().getOrDefault(DataComponents.MAX_STACK_SIZE, 64);
    }

    public short getMaxDurability() {
        return this.item.components().getOrDefault(DataComponents.MAX_DAMAGE, 0).shortValue();
    }

    public boolean isEdible() {
        return this.item.components().has(DataComponents.FOOD);
    }

    public boolean isRecord() {
        return this.item.components().has(DataComponents.JUKEBOX_PLAYABLE);
        // old: return this.item instanceof MusicDiscItem;
    }

    public boolean isFuel() {
        return IMixinMinecraftServer.getServer().fuelValues().isFuel(new net.minecraft.world.item.ItemStack(this.item));
        // return AbstractFurnaceBlockEntity.canUseAsFuel(new net.minecraft.item.ItemStack(this.item));
    }

    public boolean isCompostable() {
        return ComposterBlock.COMPOSTABLES.containsKey(this.item);
    }

    public float getCompostChance() {
        Preconditions.checkArgument(this.isCompostable(), ("The item type " + String.valueOf(this.getKey()) + " is not compostable"));
        return ComposterBlock.COMPOSTABLES.getFloat(this.item);
    }

    public ItemType getCraftingRemainingItem() {
        net.minecraft.world.item.ItemStack expectedItem = this.item.getCraftingRemainder();
        return expectedItem.isEmpty() ? null : CraftItemType.minecraftToBukkitNew(expectedItem.getItem());
    }

    @NotNull
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers() {
        return this.getDefaultAttributeModifiers((EquipmentSlotGroup sg) -> true);
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(org.bukkit.inventory.EquipmentSlot slot) {
        EquipmentSlot nmsSlot = CraftEquipmentSlot.getNMS(slot);
        return this.getDefaultAttributeModifiers((EquipmentSlotGroup sg) -> sg.test(nmsSlot));
    }
    
    private Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(final java.util.function.Predicate<net.minecraft.world.entity.EquipmentSlotGroup> slotPredicate) {
        // Paper end - improve/fix item default attribute API
        ImmutableMultimap.Builder<Attribute, AttributeModifier> defaultAttributes = ImmutableMultimap.builder();

        ItemAttributeModifiers nmsDefaultAttributes = this.item.components().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        // Paper start - improve/fix item default attribute API
        for (final net.minecraft.world.item.component.ItemAttributeModifiers.Entry entry : nmsDefaultAttributes.modifiers()) {
            if (!slotPredicate.test(entry.slot())) continue;
            final Attribute attribute = CraftAttribute.minecraftHolderToBukkit(entry.attribute());
            final AttributeModifier modifier = CraftAttributeInstance.convert(entry.modifier(), entry.slot());
            defaultAttributes.put(attribute, modifier);
        }
        // Paper end - improve/fix item default attribute API

        return defaultAttributes.build();
    }


    public CreativeCategory getCreativeCategory() {
        return CreativeCategory.BUILDING_BLOCKS;
    }

    public boolean isEnabledByFeature(@NotNull World world) {
        Preconditions.checkNotNull(world, "World cannot be null");
        return this.getHandle().isEnabled(((CraftWorld)world).getHandle().enabledFeatures());
    }

    @NotNull
    public String getTranslationKey() {
        return this.item.getDescriptionId();
    }

    public NamespacedKey getKey() {
        return this.key;
    }

    public Material asMaterial() {
        return (Material) Registry.MATERIAL.get(this.key);
    }

    public String translationKey() {
        return this.item.getDescriptionId();
    }

	@Override
	public ItemRarity getItemRarity() {
        Rarity rarity = this.item.components().get(DataComponents.RARITY);
        return rarity == null ? null : ItemRarity.valueOf((String)rarity.name());
	}

	@Override
	public <T> @Nullable T getDefaultData(@NotNull Valued<T> type) {
		return null;
		// return (T)PaperDataComponentType.convertDataComponentValue(this.item.getComponents(), (PaperDataComponentType.ValuedImpl)type);
	}

	@Override
	public boolean hasDefaultData(@NotNull DataComponentType type) {
		return this.item.components().has(PaperDataComponentType_bukkitToMinecraft(type));
	}

	@Override
	public @Unmodifiable @NotNull Set<DataComponentType> getDefaultDataTypes() {
		return PaperDataComponentType_minecraftToBukkit(this.item.components().keySet());
	}
	
	
	// TODO: move to PaperDataComponentType
	public static <T> net.minecraft.core.component.DataComponentType<T> PaperDataComponentType_bukkitToMinecraft(final DataComponentType type) {
        return CraftRegistry.bukkitToMinecraft(type);
    }
	
	public static DataComponentType PaperDataComponentType_minecraftToBukkit(final net.minecraft.core.component.DataComponentType<?> type) {
        return CraftRegistry.minecraftToBukkit(type, Registries.DATA_COMPONENT_TYPE);
    }
	
	public static Set<DataComponentType> PaperDataComponentType_minecraftToBukkit(final Set<net.minecraft.core.component.DataComponentType<?>> nmsTypes) {
        final Set<DataComponentType> types = new HashSet<>(nmsTypes.size());
        for (final net.minecraft.core.component.DataComponentType<?> nmsType : nmsTypes) {
            types.add(PaperDataComponentType_minecraftToBukkit(nmsType));
        }
        return Collections.unmodifiableSet(types);
    }

	@Override
	public int getBurnDuration() {
		net.minecraft.world.item.ItemStack stack;
        FuelValues fuelValues = CraftServer.server.fuelValues();
        if (!fuelValues.isFuel(stack = new net.minecraft.world.item.ItemStack((ItemLike)this.getHandle()))) {
            return 0;
        }
        return fuelValues.burnDuration(stack);
	}
	
}

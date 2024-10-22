package org.bukkit.craftbukkit.inventory;

import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.cardboardpowered.impl.CardboardAttributable;
import org.cardboardpowered.impl.CardboardAttributeInstance;
import org.cardboardpowered.impl.world.WorldImpl;
import org.jetbrains.annotations.NotNull;

import net.minecraft.item.Item;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.javazilla.bukkitfabric.Utils;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.block.ComposterBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Rarity;

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
        return CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.ITEM, Registry.ITEM);
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
        net.minecraft.item.ItemStack stack = new net.minecraft.item.ItemStack(this.item, amount);
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
        return this.item.getComponents().getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 64);
    }

    public short getMaxDurability() {
        return this.item.getComponents().getOrDefault(DataComponentTypes.MAX_DAMAGE, 0).shortValue();
    }

    public boolean isEdible() {
        return this.item.getComponents().contains(DataComponentTypes.FOOD);
    }

    public boolean isRecord() {
        return this.item.getComponents().contains(DataComponentTypes.JUKEBOX_PLAYABLE);
        // old: return this.item instanceof MusicDiscItem;
    }

    public boolean isFuel() {
        return AbstractFurnaceBlockEntity.canUseAsFuel(new net.minecraft.item.ItemStack(this.item));
    }

    public boolean isCompostable() {
        return ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(this.item);
    }

    public float getCompostChance() {
        Preconditions.checkArgument(this.isCompostable(), ("The item type " + String.valueOf(this.getKey()) + " is not compostable"));
        return ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(this.item);
    }

    public ItemType getCraftingRemainingItem() {
        Item expectedItem = this.item.getRecipeRemainder();
        return expectedItem == null ? null : CraftItemType.minecraftToBukkitNew(expectedItem);
    }

    @NotNull
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers() {
        return this.getDefaultAttributeModifiers((AttributeModifierSlot sg) -> true);
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(org.bukkit.inventory.EquipmentSlot slot) {
        EquipmentSlot nmsSlot = Utils.getNMS(slot);
        return this.getDefaultAttributeModifiers((AttributeModifierSlot sg) -> sg.matches(nmsSlot));
    }

    private Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(Predicate<AttributeModifierSlot> slotPredicate) {
        ImmutableMultimap.Builder defaultAttributes = ImmutableMultimap.builder();
        AttributeModifiersComponent nmsDefaultAttributes = this.item.getComponents().getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        if (nmsDefaultAttributes.modifiers().isEmpty()) {
            nmsDefaultAttributes = this.item.getAttributeModifiers();
        }
        for (AttributeModifiersComponent.Entry entry : nmsDefaultAttributes.modifiers()) {
            if (!slotPredicate.test(entry.slot())) continue;
            Attribute attribute = CardboardAttributable.minecraftHolderToBukkit(entry.attribute());
            AttributeModifier modifier = CardboardAttributeInstance.convert(entry.modifier(), entry.slot());
            defaultAttributes.put(attribute, modifier);
        }
        return defaultAttributes.build();
    }

    public CreativeCategory getCreativeCategory() {
        return CreativeCategory.BUILDING_BLOCKS;
    }

    public boolean isEnabledByFeature(@NotNull World world) {
        Preconditions.checkNotNull(world, "World cannot be null");
        return this.getHandle().isEnabled(((WorldImpl)world).getHandle().getEnabledFeatures());
    }

    @NotNull
    public String getTranslationKey() {
        return this.item.getTranslationKey();
    }

    public NamespacedKey getKey() {
        return this.key;
    }

    public Material asMaterial() {
        return (Material) Registry.MATERIAL.get(this.key);
    }

    public String translationKey() {
        return this.item.getTranslationKey();
    }

	@Override
	public ItemRarity getItemRarity() {
        Rarity rarity = this.item.getComponents().get(DataComponentTypes.RARITY);
        return rarity == null ? null : ItemRarity.valueOf((String)rarity.name());
	}
	
}

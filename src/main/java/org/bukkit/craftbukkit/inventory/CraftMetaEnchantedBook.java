package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftMetaItem;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

@DelegateDeserialization(value=SerializableMeta.class)
class CraftMetaEnchantedBook
extends CraftMetaItem
implements EnchantmentStorageMeta {
    static final CraftMetaItem.ItemMetaKeyType<ItemEnchantmentsComponent> STORED_ENCHANTMENTS = new CraftMetaItem.ItemMetaKeyType<ItemEnchantmentsComponent>(DataComponentTypes.STORED_ENCHANTMENTS, "stored-enchants");
    private Map<Enchantment, Integer> enchantments;

    CraftMetaEnchantedBook(CraftMetaItem meta) {
        super(meta);
        if (!(meta instanceof CraftMetaEnchantedBook)) {
            return;
        }
        CraftMetaEnchantedBook that = (CraftMetaEnchantedBook)meta;
        if (that.hasEnchants()) {
            this.enchantments = new LinkedHashMap<Enchantment, Integer>(that.enchantments);
        }
    }

    CraftMetaEnchantedBook(ComponentChanges tag, Set<DataComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaEnchantedBook.getOrEmpty(tag, STORED_ENCHANTMENTS).ifPresent(itemEnchantments -> {
            this.enchantments = CraftMetaEnchantedBook.buildEnchantments(itemEnchantments);
            // TODO: 1.20.6
            //if (!itemEnchantments.showInTooltip) {
            //    this.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            //}
        });
    }

    CraftMetaEnchantedBook(Map<String, Object> map) {
        super(map);
        this.enchantments = CraftMetaEnchantedBook.buildEnchantments(map, STORED_ENCHANTMENTS);
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator itemTag) {
        super.applyToItem(itemTag);
        this.applyEnchantments(this.enchantments, itemTag, STORED_ENCHANTMENTS, ItemFlag.HIDE_ENCHANTS);
    }

    @Override
    boolean applicableTo(Material type) {
        return type == Material.ENCHANTED_BOOK;
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.isEnchantedEmpty();
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaEnchantedBook) {
            CraftMetaEnchantedBook that = (CraftMetaEnchantedBook)meta;
            return this.hasStoredEnchants() ? that.hasStoredEnchants() && this.enchantments.equals(that.enchantments) : !that.hasStoredEnchants();
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaEnchantedBook || this.isEnchantedEmpty());
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.hasStoredEnchants()) {
            hash = 61 * hash + this.enchantments.hashCode();
        }
        return original != hash ? CraftMetaEnchantedBook.class.hashCode() ^ hash : hash;
    }

    @Override
    public CraftMetaEnchantedBook clone() {
        CraftMetaEnchantedBook meta = (CraftMetaEnchantedBook)super.clone();
        if (this.enchantments != null) {
            meta.enchantments = new LinkedHashMap<Enchantment, Integer>(this.enchantments);
        }
        return meta;
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        CraftMetaEnchantedBook.serializeEnchantments(this.enchantments, builder, STORED_ENCHANTMENTS);
        return builder;
    }

    boolean isEnchantedEmpty() {
        return !this.hasStoredEnchants();
    }

    public boolean hasStoredEnchant(Enchantment ench) {
        return this.hasStoredEnchants() && this.enchantments.containsKey(ench);
    }

    public int getStoredEnchantLevel(Enchantment ench) {
        Integer level;
        Integer n = level = this.hasStoredEnchants() ? this.enchantments.get(ench) : null;
        if (level == null) {
            return 0;
        }
        return level;
    }

    public Map<Enchantment, Integer> getStoredEnchants() {
        return this.hasStoredEnchants() ? ImmutableMap.copyOf(this.enchantments) : ImmutableMap.of();
    }

    public boolean addStoredEnchant(Enchantment ench, int level, boolean ignoreRestrictions) {
        if (this.enchantments == null) {
            this.enchantments = new LinkedHashMap<Enchantment, Integer>(4);
        }
        if (ignoreRestrictions || level >= ench.getStartLevel() && level <= ench.getMaxLevel()) {
            Integer old = this.enchantments.put(ench, level);
            return old == null || old != level;
        }
        return false;
    }

    public boolean removeStoredEnchant(Enchantment ench) {
        return this.hasStoredEnchants() && this.enchantments.remove(ench) != null;
    }

    public boolean hasStoredEnchants() {
        return this.enchantments != null && !this.enchantments.isEmpty();
    }

    public boolean hasConflictingStoredEnchant(Enchantment ench) {
        return CraftMetaEnchantedBook.checkConflictingEnchants(this.enchantments, ench);
    }
}


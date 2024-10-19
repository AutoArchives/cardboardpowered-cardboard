package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftItemFactory;
import org.bukkit.craftbukkit.inventory.CraftMetaItem;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.LeatherArmorMeta;

@DelegateDeserialization(value=SerializableMeta.class)
class CraftMetaLeatherArmor
extends CraftMetaItem
implements LeatherArmorMeta {
    private static final Set<Material> LEATHER_ARMOR_MATERIALS = Sets.newHashSet(
    		new Material[]{
    				Material.LEATHER_HELMET,
    				Material.LEATHER_HORSE_ARMOR,
    				Material.LEATHER_CHESTPLATE,
    				Material.LEATHER_LEGGINGS,
    				Material.LEATHER_BOOTS,
    				// TODO: 1.20.6
    				// Material.WOLF_ARMOR
    			}
    	);
    static final CraftMetaItem.ItemMetaKeyType<DyedColorComponent> COLOR = new CraftMetaItem.ItemMetaKeyType<DyedColorComponent>(DataComponentTypes.DYED_COLOR, "color");
    private Color color = CraftItemFactory.DEFAULT_LEATHER_COLOR;

    CraftMetaLeatherArmor(CraftMetaItem meta) {
        super(meta);
        CraftMetaLeatherArmor.readColor((LeatherArmorMeta)this, meta);
    }

    CraftMetaLeatherArmor(ComponentChanges tag, Set<ComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaLeatherArmor.readColor((LeatherArmorMeta)this, tag);
    }

    CraftMetaLeatherArmor(Map<String, Object> map) {
        super(map);
        CraftMetaLeatherArmor.readColor((LeatherArmorMeta)this, map);
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator itemTag) {
        super.applyToItem(itemTag);
        CraftMetaLeatherArmor.applyColor(this, itemTag);
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.isLeatherArmorEmpty();
    }

    boolean isLeatherArmorEmpty() {
        return !this.hasColor();
    }

    @Override
    boolean applicableTo(Material type) {
        return LEATHER_ARMOR_MATERIALS.contains(type);
    }

    @Override
    public CraftMetaLeatherArmor clone() {
        return (CraftMetaLeatherArmor)super.clone();
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color == null ? CraftItemFactory.DEFAULT_LEATHER_COLOR : color;
    }

    boolean hasColor() {
        return CraftMetaLeatherArmor.hasColor(this);
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        CraftMetaLeatherArmor.serialize(this, builder);
        return builder;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaLeatherArmor) {
            CraftMetaLeatherArmor that = (CraftMetaLeatherArmor)meta;
            return this.color.equals(that.color);
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaLeatherArmor || this.isLeatherArmorEmpty());
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.hasColor()) {
            hash ^= this.color.hashCode();
        }
        return original != hash ? CraftMetaLeatherArmor.class.hashCode() ^ hash : hash;
    }

    static void readColor(LeatherArmorMeta meta, CraftMetaItem other) {
        if (!(other instanceof CraftMetaLeatherArmor)) {
            return;
        }
        CraftMetaLeatherArmor armorMeta = (CraftMetaLeatherArmor)other;
        meta.setColor(armorMeta.color);
    }

    static void readColor(LeatherArmorMeta meta, ComponentChanges tag) {
        CraftMetaLeatherArmor.getOrEmpty(tag, COLOR).ifPresent(dyedItemColor -> {
            if (!dyedItemColor.showInTooltip()) {
                meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_DYE});
            }
            try {
                meta.setColor(Color.fromRGB((int)dyedItemColor.rgb()));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        });
    }

    static void readColor(LeatherArmorMeta meta, Map<String, Object> map) {
        meta.setColor(org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getObject(Color.class, map, CraftMetaLeatherArmor.COLOR.BUKKIT, true));
    }

    static boolean hasColor(LeatherArmorMeta meta) {
        return !CraftItemFactory.DEFAULT_LEATHER_COLOR.equals(meta.getColor());
    }

    static void applyColor(LeatherArmorMeta meta, CraftMetaItem.Applicator tag) {
        if (CraftMetaLeatherArmor.hasColor(meta)) {
            tag.put(COLOR, new DyedColorComponent(meta.getColor().asRGB(), !meta.hasItemFlag(ItemFlag.HIDE_DYE)));
        }
    }

    static void serialize(LeatherArmorMeta meta, ImmutableMap.Builder<String, Object> builder) {
        if (CraftMetaLeatherArmor.hasColor(meta)) {
            builder.put(CraftMetaLeatherArmor.COLOR.BUKKIT, meta.getColor());
        }
    }

    public boolean isDyed() {
        return this.hasColor();
    }
}


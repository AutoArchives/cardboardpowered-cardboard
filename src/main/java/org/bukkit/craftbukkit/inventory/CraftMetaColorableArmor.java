package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.DyedColorComponent;
import org.bukkit.Color;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

@DelegateDeserialization(value=SerializableMeta.class)
public class CraftMetaColorableArmor extends CraftMetaArmor implements ColorableArmorMeta {

    private Integer color;

    CraftMetaColorableArmor(CraftMetaItem meta) {
        super(meta);
        if (!(meta instanceof CraftMetaColorableArmor)) {
            return;
        }
        CraftMetaColorableArmor armorMeta = (CraftMetaColorableArmor)meta;
        this.color = armorMeta.color;
    }

    CraftMetaColorableArmor(ComponentChanges tag, Set<ComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaColorableArmor.getOrEmpty(tag, CraftMetaLeatherArmor.COLOR).ifPresent(dyedItemColor -> {
            if (!dyedItemColor.showInTooltip()) {
                this.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_DYE});
            }
            this.color = dyedItemColor.rgb();
        });
    }

    CraftMetaColorableArmor(Map<String, Object> map) {
        super(map);
        CraftMetaLeatherArmor.readColor((LeatherArmorMeta)this, map);
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator itemTag) {
        super.applyToItem(itemTag);
        if (this.hasColor()) {
            itemTag.put(CraftMetaLeatherArmor.COLOR, new DyedColorComponent(this.color, !this.hasItemFlag(ItemFlag.HIDE_DYE)));
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.isLeatherArmorEmpty();
    }

    boolean isLeatherArmorEmpty() {
        return !this.hasColor();
    }

    @Override
    public CraftMetaColorableArmor clone() {
        CraftMetaColorableArmor clone = (CraftMetaColorableArmor)super.clone();
        clone.color = this.color;
        return clone;
    }

    public Color getColor() {
        return this.color == null ? CraftItemFactory.DEFAULT_LEATHER_COLOR : Color.fromRGB((int)(this.color & 0xFFFFFF));
    }

    public void setColor(Color color) {
        this.color = color == null ? null : Integer.valueOf(color.asRGB());
    }

    boolean hasColor() {
        return this.color != null;
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        CraftMetaLeatherArmor.serialize((LeatherArmorMeta)this, builder);
        return builder;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaColorableArmor) {
            CraftMetaColorableArmor that = (CraftMetaColorableArmor)meta;
            return this.hasColor() ? that.hasColor() && this.color.equals(that.color) : !that.hasColor();
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaColorableArmor || this.isLeatherArmorEmpty());
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.hasColor()) {
            hash ^= this.color.hashCode();
        }
        return original != hash ? CraftMetaColorableArmor.class.hashCode() ^ hash : hash;
    }

    public boolean isDyed() {
        return this.hasColor();
    }
}


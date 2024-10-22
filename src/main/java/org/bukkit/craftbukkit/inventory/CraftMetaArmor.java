package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import io.papermc.paper.registry.RegistryKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.inventory.CraftMetaItem;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimMaterial;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimPattern;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

@DelegateDeserialization(value=SerializableMeta.class)
public class CraftMetaArmor
extends CraftMetaItem
implements ArmorMeta {
    static final CraftMetaItem.ItemMetaKeyType<net.minecraft.item.trim.ArmorTrim> TRIM = new CraftMetaItem.ItemMetaKeyType<net.minecraft.item.trim.ArmorTrim>(DataComponentTypes.TRIM, "trim");
    static final CraftMetaItem.ItemMetaKey TRIM_MATERIAL = new CraftMetaItem.ItemMetaKey("material");
    static final CraftMetaItem.ItemMetaKey TRIM_PATTERN = new CraftMetaItem.ItemMetaKey("pattern");
    private ArmorTrim trim;

    CraftMetaArmor(CraftMetaItem meta) {
        super(meta);
        if (meta instanceof CraftMetaArmor) {
            CraftMetaArmor armorMeta = (CraftMetaArmor)meta;
            this.trim = armorMeta.trim;
        }
    }

    CraftMetaArmor(ComponentChanges tag, Set<ComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaArmor.getOrEmpty(tag, TRIM).ifPresent(trimCompound -> {
            TrimMaterial trimMaterial = CraftRegistry.unwrapAndConvertHolder(RegistryKey.TRIM_MATERIAL, trimCompound.getMaterial()).orElse(null);
            TrimPattern trimPattern = CraftRegistry.unwrapAndConvertHolder(RegistryKey.TRIM_PATTERN, trimCompound.getPattern()).orElse(null);
            if (trimMaterial == null || trimPattern == null) {
                return;
            }
            this.trim = new ArmorTrim(trimMaterial, trimPattern);
            //if (!trimCompound.showInTooltip) {
            //    this.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ARMOR_TRIM});
            //}
        });
    }

    CraftMetaArmor(Map<String, Object> map) {
        super(map);
        Map trimData = SerializableMeta.getObject(Map.class, map, CraftMetaArmor.TRIM.BUKKIT, true);
        if (trimData != null) {
            String materialKeyString = SerializableMeta.getString(trimData, CraftMetaArmor.TRIM_MATERIAL.BUKKIT, true);
            String patternKeyString = SerializableMeta.getString(trimData, CraftMetaArmor.TRIM_PATTERN.BUKKIT, true);
            if (materialKeyString != null && patternKeyString != null) {
                NamespacedKey materialKey = NamespacedKey.fromString((String)materialKeyString);
                NamespacedKey patternKey = NamespacedKey.fromString((String)patternKeyString);
                if (materialKey != null && patternKey != null) {
                    TrimMaterial trimMaterial = (TrimMaterial)Registry.TRIM_MATERIAL.get(materialKey);
                    TrimPattern trimPattern = (TrimPattern)Registry.TRIM_PATTERN.get(patternKey);
                    if (trimMaterial != null && trimPattern != null) {
                        this.trim = new ArmorTrim(trimMaterial, trimPattern);
                    }
                }
            }
        }
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator itemTag) {
        super.applyToItem(itemTag);
        if (this.hasTrim()) {
            itemTag.put(TRIM, new net.minecraft.item.trim.ArmorTrim(CraftTrimMaterial.bukkitToMinecraftHolder(this.trim.getMaterial()), CraftTrimPattern.bukkitToMinecraftHolder(this.trim.getPattern()), !this.hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM)));
        }
    }

    @Override
    boolean equalsCommon(CraftMetaItem that) {
        if (!super.equalsCommon(that)) {
            return false;
        }
        if (that instanceof CraftMetaArmor) {
            CraftMetaArmor armorMeta = (CraftMetaArmor)that;
            return Objects.equals(this.trim, armorMeta.trim);
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaArmor || this.isArmorEmpty());
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.isArmorEmpty();
    }

    private boolean isArmorEmpty() {
        return !this.hasTrim();
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.hasTrim()) {
            hash = 61 * hash + this.trim.hashCode();
        }
        return original != hash ? CraftMetaArmor.class.hashCode() ^ hash : hash;
    }

    @Override
    public CraftMetaArmor clone() {
        CraftMetaArmor meta = (CraftMetaArmor)super.clone();
        meta.trim = this.trim;
        return meta;
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        if (this.hasTrim()) {
            HashMap<String, String> trimData = new HashMap<String, String>();
            trimData.put(CraftMetaArmor.TRIM_MATERIAL.BUKKIT, this.trim.getMaterial().getKey().toString());
            trimData.put(CraftMetaArmor.TRIM_PATTERN.BUKKIT, this.trim.getPattern().getKey().toString());
            builder.put(CraftMetaArmor.TRIM.BUKKIT, trimData);
        }
        return builder;
    }

    public boolean hasTrim() {
        return this.trim != null;
    }

    public void setTrim(ArmorTrim trim) {
        this.trim = trim;
    }

    public ArmorTrim getTrim() {
        return this.trim;
    }
}


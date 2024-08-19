package org.bukkit.craftbukkit.inventory;

import com.destroystokyo.paper.inventory.meta.ArmorStandMeta;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;

@DelegateDeserialization(value=SerializableMeta.class)
public class CraftMetaArmorStand
extends CraftMetaItem
implements ArmorStandMeta {
    static final CraftMetaItem.ItemMetaKeyType<NbtComponent> ENTITY_TAG = new CraftMetaItem.ItemMetaKeyType<NbtComponent>(DataComponentTypes.ENTITY_DATA, "entity-tag");
    static final CraftMetaItem.ItemMetaKey INVISIBLE = new CraftMetaItem.ItemMetaKey("Invisible", "invisible");
    static final CraftMetaItem.ItemMetaKey NO_BASE_PLATE = new CraftMetaItem.ItemMetaKey("NoBasePlate", "no-base-plate");
    static final CraftMetaItem.ItemMetaKey SHOW_ARMS = new CraftMetaItem.ItemMetaKey("ShowArms", "show-arms");
    static final CraftMetaItem.ItemMetaKey SMALL = new CraftMetaItem.ItemMetaKey("Small", "small");
    static final CraftMetaItem.ItemMetaKey MARKER = new CraftMetaItem.ItemMetaKey("Marker", "marker");
    private Boolean invisible = null;
    private Boolean noBasePlate = null;
    private Boolean showArms = null;
    private Boolean small = null;
    private Boolean marker = null;
    NbtCompound entityTag;

    CraftMetaArmorStand(CraftMetaItem meta) {
        super(meta);
        if (!(meta instanceof CraftMetaArmorStand)) {
            return;
        }
        CraftMetaArmorStand armorStand = (CraftMetaArmorStand)meta;
        this.invisible = armorStand.invisible;
        this.noBasePlate = armorStand.noBasePlate;
        this.showArms = armorStand.showArms;
        this.small = armorStand.small;
        this.marker = armorStand.marker;
        this.entityTag = armorStand.entityTag;
    }

    CraftMetaArmorStand(ComponentChanges tag, Set<DataComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaArmorStand.getOrEmpty(tag, ENTITY_TAG).ifPresent(nbt -> {
            this.entityTag = nbt.copyNbt();
            if (this.entityTag.contains(CraftMetaArmorStand.INVISIBLE.NBT)) {
                this.invisible = this.entityTag.getBoolean(CraftMetaArmorStand.INVISIBLE.NBT);
            }
            if (this.entityTag.contains(CraftMetaArmorStand.NO_BASE_PLATE.NBT)) {
                this.noBasePlate = this.entityTag.getBoolean(CraftMetaArmorStand.NO_BASE_PLATE.NBT);
            }
            if (this.entityTag.contains(CraftMetaArmorStand.SHOW_ARMS.NBT)) {
                this.showArms = this.entityTag.getBoolean(CraftMetaArmorStand.SHOW_ARMS.NBT);
            }
            if (this.entityTag.contains(CraftMetaArmorStand.SMALL.NBT)) {
                this.small = this.entityTag.getBoolean(CraftMetaArmorStand.SMALL.NBT);
            }
            if (this.entityTag.contains(CraftMetaArmorStand.MARKER.NBT)) {
                this.marker = this.entityTag.getBoolean(CraftMetaArmorStand.MARKER.NBT);
            }
        });
    }

    CraftMetaArmorStand(Map<String, Object> map) {
        super(map);
        this.invisible = SerializableMeta.getBoolean(map, CraftMetaArmorStand.INVISIBLE.BUKKIT);
        this.noBasePlate = SerializableMeta.getBoolean(map, CraftMetaArmorStand.NO_BASE_PLATE.BUKKIT);
        this.showArms = SerializableMeta.getBoolean(map, CraftMetaArmorStand.SHOW_ARMS.BUKKIT);
        this.small = SerializableMeta.getBoolean(map, CraftMetaArmorStand.SMALL.BUKKIT);
        this.marker = SerializableMeta.getBoolean(map, CraftMetaArmorStand.MARKER.BUKKIT);
    }

    @Override
    void deserializeInternal(NbtCompound tag, Object context) {
        super.deserializeInternal(tag, context);
        if (tag.contains(CraftMetaArmorStand.ENTITY_TAG.NBT)) {
            this.entityTag = tag.getCompound(CraftMetaArmorStand.ENTITY_TAG.NBT);
        }
    }

    @Override
    void serializeInternal(Map<String, NbtElement> internalTags) {
        if (this.entityTag != null && !this.entityTag.isEmpty()) {
            internalTags.put(CraftMetaArmorStand.ENTITY_TAG.NBT, this.entityTag);
        }
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator tag) {
        super.applyToItem(tag);
        if (!this.isArmorStandEmpty() && this.entityTag == null) {
            this.entityTag = new NbtCompound();
        }
        if (this.invisible != null) {
            this.entityTag.putBoolean(CraftMetaArmorStand.INVISIBLE.NBT, this.invisible);
        }
        if (this.noBasePlate != null) {
            this.entityTag.putBoolean(CraftMetaArmorStand.NO_BASE_PLATE.NBT, this.noBasePlate);
        }
        if (this.showArms != null) {
            this.entityTag.putBoolean(CraftMetaArmorStand.SHOW_ARMS.NBT, this.showArms);
        }
        if (this.small != null) {
            this.entityTag.putBoolean(CraftMetaArmorStand.SMALL.NBT, this.small);
        }
        if (this.marker != null) {
            this.entityTag.putBoolean(CraftMetaArmorStand.MARKER.NBT, this.marker);
        }
        if (this.entityTag != null) {
            tag.put(ENTITY_TAG, NbtComponent.of(this.entityTag));
        }
    }

    @Override
    boolean applicableTo(Material type) {
        return type == Material.ARMOR_STAND;
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.isArmorStandEmpty();
    }

    boolean isArmorStandEmpty() {
        return this.invisible == null && this.noBasePlate == null && this.showArms == null && this.small == null && this.marker == null && this.entityTag == null;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaArmorStand) {
            CraftMetaArmorStand that = (CraftMetaArmorStand)meta;
            return this.invisible == that.invisible && this.noBasePlate == that.noBasePlate && this.showArms == that.showArms && this.small == that.small && this.marker == that.marker;
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaArmorStand || this.isArmorStandEmpty());
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        return original != (hash += this.isMarker() ? 61 * (hash += this.isSmall() ? 61 * (hash += this.shouldShowArms() ? 61 * (hash += this.hasNoBasePlate() ? 61 * (hash += this.isInvisible() ? 61 * (hash += this.entityTag != null ? 73 * hash + this.entityTag.hashCode() : 0) + 1231 : 0) + 1231 : 0) + 1231 : 0) + 1231 : 0) + 1231 : 0) ? CraftMetaArmorStand.class.hashCode() ^ hash : hash;
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        if (this.invisible != null) {
            builder.put(CraftMetaArmorStand.INVISIBLE.BUKKIT, this.invisible);
        }
        if (this.noBasePlate != null) {
            builder.put(CraftMetaArmorStand.NO_BASE_PLATE.BUKKIT, this.noBasePlate);
        }
        if (this.showArms != null) {
            builder.put(CraftMetaArmorStand.SHOW_ARMS.BUKKIT, this.showArms);
        }
        if (this.small != null) {
            builder.put(CraftMetaArmorStand.SMALL.BUKKIT, this.small);
        }
        if (this.marker != null) {
            builder.put(CraftMetaArmorStand.MARKER.BUKKIT, this.marker);
        }
        return builder;
    }

    @Override
    public CraftMetaArmorStand clone() {
        CraftMetaArmorStand clone = (CraftMetaArmorStand)super.clone();
        if (this.entityTag != null) {
            clone.entityTag = this.entityTag.copy();
        }
        return clone;
    }

    public boolean isInvisible() {
        return this.invisible != null && this.invisible != false;
    }

    public boolean hasNoBasePlate() {
        return this.noBasePlate != null && this.noBasePlate != false;
    }

    public boolean shouldShowArms() {
        return this.showArms != null && this.showArms != false;
    }

    public boolean isSmall() {
        return this.small != null && this.small != false;
    }

    public boolean isMarker() {
        return this.marker != null && this.marker != false;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public void setNoBasePlate(boolean noBasePlate) {
        this.noBasePlate = noBasePlate;
    }

    public void setShowArms(boolean showArms) {
        this.showArms = showArms;
    }

    public void setSmall(boolean small) {
        this.small = small;
    }

    public void setMarker(boolean marker) {
        this.marker = marker;
    }
}


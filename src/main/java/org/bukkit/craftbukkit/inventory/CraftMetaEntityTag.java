package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;

@DelegateDeserialization(value=SerializableMeta.class)
public class CraftMetaEntityTag extends CraftMetaItem {

    private static final Set<Material> ENTITY_TAGGABLE_MATERIALS = Sets.newHashSet(
    	new Material[]{
    		Material.COD_BUCKET,
    		Material.PUFFERFISH_BUCKET,
    		Material.SALMON_BUCKET,
    		Material.TADPOLE_BUCKET, 
    		Material.ITEM_FRAME,
    		Material.GLOW_ITEM_FRAME,
    		Material.PAINTING
    	}
    );

    static final CraftMetaItem.ItemMetaKeyType<NbtComponent> ENTITY_TAG = new CraftMetaItem.ItemMetaKeyType<NbtComponent>(DataComponentTypes.ENTITY_DATA, "EntityTag", "entity-tag");
    NbtCompound entityTag;

    CraftMetaEntityTag(CraftMetaItem meta) {
        super(meta);
        if (!(meta instanceof CraftMetaEntityTag)) {
            return;
        }
        CraftMetaEntityTag entity = (CraftMetaEntityTag)meta;
        this.entityTag = entity.entityTag;
    }

    CraftMetaEntityTag(ComponentChanges tag, Set<ComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaEntityTag.getOrEmpty(tag, ENTITY_TAG).ifPresent(nbt -> {
            this.entityTag = nbt.copyNbt();
        });
    }

    CraftMetaEntityTag(Map<String, Object> map) {
        super(map);
    }

    @Override
    void deserializeInternal(NbtCompound tag, Object context) {
        super.deserializeInternal(tag, context);
        if (tag.contains(CraftMetaEntityTag.ENTITY_TAG.NBT)) {
            this.entityTag = tag.getCompound(CraftMetaEntityTag.ENTITY_TAG.NBT);
        }
    }

    @Override
    void serializeInternal(Map<String, NbtElement> internalTags) {
        if (this.entityTag != null && !this.entityTag.isEmpty()) {
            internalTags.put(CraftMetaEntityTag.ENTITY_TAG.NBT, this.entityTag);
        }
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator tag) {
        super.applyToItem(tag);
        if (this.entityTag != null) {
            tag.put(ENTITY_TAG, NbtComponent.of(this.entityTag));
        }
    }

    @Override
    boolean applicableTo(Material type) {
        return ENTITY_TAGGABLE_MATERIALS.contains(type);
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.isEntityTagEmpty();
    }

    boolean isEntityTagEmpty() {
        return this.entityTag == null;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaEntityTag) {
            CraftMetaEntityTag that = (CraftMetaEntityTag)meta;
            return this.entityTag != null ? that.entityTag != null && this.entityTag.equals(that.entityTag) : that.entityTag == null;
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaEntityTag || this.isEntityTagEmpty());
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.entityTag != null) {
            hash = 73 * hash + this.entityTag.hashCode();
        }
        return original != hash ? CraftMetaEntityTag.class.hashCode() ^ hash : hash;
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        return builder;
    }

    @Override
    public CraftMetaEntityTag clone() {
        CraftMetaEntityTag clone = (CraftMetaEntityTag)super.clone();
        if (this.entityTag != null) {
            clone.entityTag = this.entityTag.copy();
        }
        return clone;
    }

}


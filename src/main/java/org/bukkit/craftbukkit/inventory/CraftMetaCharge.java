package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftMetaFirework;
import org.bukkit.craftbukkit.inventory.CraftMetaItem;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;

@DelegateDeserialization(value=SerializableMeta.class)
class CraftMetaCharge
extends CraftMetaItem
implements FireworkEffectMeta {
    static final CraftMetaItem.ItemMetaKeyType<FireworkExplosionComponent> EXPLOSION = new CraftMetaItem.ItemMetaKeyType<FireworkExplosionComponent>(DataComponentTypes.FIREWORK_EXPLOSION, "firework-effect");
    private FireworkEffect effect;

    CraftMetaCharge(CraftMetaItem meta) {
        super(meta);
        if (meta instanceof CraftMetaCharge) {
            this.effect = ((CraftMetaCharge)meta).effect;
        }
    }

    CraftMetaCharge(Map<String, Object> map) {
        super(map);
        this.setEffect(org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getObject(FireworkEffect.class, map, CraftMetaCharge.EXPLOSION.BUKKIT, true));
    }

    CraftMetaCharge(ComponentChanges tag, Set<ComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaCharge.getOrEmpty(tag, EXPLOSION).ifPresent(f2 -> {
            try {
                this.effect = CraftMetaFirework.getEffect(f2);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        });
    }

    public void setEffect(FireworkEffect effect) {
        this.effect = effect;
    }

    public boolean hasEffect() {
        return this.effect != null;
    }

    public FireworkEffect getEffect() {
        return this.effect;
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator itemTag) {
        super.applyToItem(itemTag);
        if (this.hasEffect()) {
            itemTag.put(EXPLOSION, CraftMetaFirework.getExplosion(this.effect));
        }
    }

    @Override
    boolean applicableTo(Material type) {
        return type == Material.FIREWORK_STAR;
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && !this.hasChargeMeta();
    }

    boolean hasChargeMeta() {
        return this.hasEffect();
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaCharge) {
            CraftMetaCharge that = (CraftMetaCharge)meta;
            return this.hasEffect() ? that.hasEffect() && this.effect.equals((Object)that.effect) : !that.hasEffect();
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaCharge || !this.hasChargeMeta());
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.hasEffect()) {
            hash = 61 * hash + this.effect.hashCode();
        }
        return hash != original ? CraftMetaCharge.class.hashCode() ^ hash : hash;
    }

    @Override
    public CraftMetaCharge clone() {
        return (CraftMetaCharge)super.clone();
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        if (this.hasEffect()) {
            builder.put(CraftMetaCharge.EXPLOSION.BUKKIT, this.effect);
        }
        return builder;
    }
}


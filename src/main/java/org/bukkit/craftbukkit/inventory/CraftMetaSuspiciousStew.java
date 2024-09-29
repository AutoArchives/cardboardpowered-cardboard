package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap.Builder;

import io.papermc.paper.potion.SuspiciousEffectEntry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaSuspiciousStew extends CraftMetaItem implements SuspiciousStewMeta {

    static final ItemMetaKeyType<SuspiciousStewEffectsComponent> EFFECTS = new ItemMetaKeyType<>(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, "effects");

    // private List<PotionEffect> customEffects;

    private List<SuspiciousEffectEntry> customEffects;
    
    CraftMetaSuspiciousStew(CraftMetaItem meta) {
        super(meta);
        if (!(meta instanceof CraftMetaSuspiciousStew stewMeta)) {
            return;
        }
        if (stewMeta.hasCustomEffects()) {
            this.customEffects = new ArrayList<>(stewMeta.customEffects);
        }
    }

    CraftMetaSuspiciousStew(ComponentChanges tag, Set<DataComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        getOrEmpty(tag, CraftMetaSuspiciousStew.EFFECTS).ifPresent((suspiciousStewEffects) -> {
            List<SuspiciousStewEffectsComponent.StewEffect> list = suspiciousStewEffects.effects();
            int length = list.size();
            this.customEffects = new ArrayList<>(length);

            for (int i = 0; i < length; i++) {
                SuspiciousStewEffectsComponent.StewEffect effect = list.get(i);
                PotionEffectType type = CraftPotionEffectType.minecraftHolderToBukkit(effect.effect());
                if (type == null) {
                    continue;
                }
                int duration = effect.duration();
                this.customEffects.add(SuspiciousEffectEntry.create((PotionEffectType)type, (int)duration));
            }
        });
    }

    CraftMetaSuspiciousStew(Map<String, Object> map) {
        super(map);

        Iterable<?> rawEffectList = SerializableMeta.getObject(Iterable.class, map, CraftMetaSuspiciousStew.EFFECTS.BUKKIT, true);
        if (rawEffectList == null) {
            return;
        }

        for (Object obj : rawEffectList) {
            Preconditions.checkArgument(obj instanceof PotionEffect, "Object (%s) in effect list is not valid", obj.getClass());
            this.addCustomEffect((PotionEffect) obj, true);
        }
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator tag) {
        super.applyToItem(tag);
        if (this.customEffects != null) {
            ArrayList<SuspiciousStewEffectsComponent.StewEffect> effectList = new ArrayList<SuspiciousStewEffectsComponent.StewEffect>();
            for (SuspiciousEffectEntry effect : this.customEffects) {
                effectList.add(new SuspiciousStewEffectsComponent.StewEffect(CraftPotionEffectType.bukkitToMinecraftHolder(effect.effect()), effect.duration()));
            }
            tag.put(EFFECTS, new SuspiciousStewEffectsComponent(effectList));
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.isStewEmpty();
    }

    boolean isStewEmpty() {
        return !this.hasCustomEffects();
    }

    @Override
    boolean applicableTo(Material type) {
        return type == Material.SUSPICIOUS_STEW;
    }

    @Override
    public CraftMetaSuspiciousStew clone() {
        CraftMetaSuspiciousStew clone = ((CraftMetaSuspiciousStew) super.clone());
        if (this.customEffects != null) {
            clone.customEffects = new ArrayList<>(this.customEffects);
        }
        return clone;
    }

    @Override
    public boolean hasCustomEffects() {
        return this.customEffects != null;
    }

    @Override
    public List<PotionEffect> getCustomEffects() {
        if (this.hasCustomEffects()) {
            return this.customEffects.stream().map(suspiciousEffectEntry -> suspiciousEffectEntry.effect().createEffect(suspiciousEffectEntry.duration(), 0)).toList();
        }
        return ImmutableList.of();
    }

    @Override
    public boolean addCustomEffect(PotionEffect effect, boolean overwrite) {
        return this.addCustomEffect(SuspiciousEffectEntry.create((PotionEffectType)effect.getType(), (int)effect.getDuration()), overwrite);

    }

    @Override
    public boolean removeCustomEffect(PotionEffectType type) {
        Preconditions.checkArgument(type != null, "Potion effect type cannot be null");

        if (!this.hasCustomEffects()) {
            return false;
        }
        boolean changed = false;
        Iterator<SuspiciousEffectEntry> iterator = this.customEffects.iterator();
        while (iterator.hasNext()) {
            SuspiciousEffectEntry effect = iterator.next();
            if (!type.equals(effect.effect())) continue;
            iterator.remove();
            changed = true;
        }
        if (this.customEffects.isEmpty()) {
            this.customEffects = null;
        }
        return changed;
    }

    @Override
    public boolean hasCustomEffect(PotionEffectType type) {
        Preconditions.checkArgument(type != null, "Potion effect type cannot be null");
        return this.indexOfEffect(type) != -1;
    }

    private int indexOfEffect(PotionEffectType type) {
        if (!this.hasCustomEffects()) {
            return -1;
        }
        for (int i2 = 0; i2 < this.customEffects.size(); ++i2) {
            if (!this.customEffects.get(i2).effect().equals(type)) continue;
            return i2;
        }
        return -1;
    }

    @Override
    public boolean clearCustomEffects() {
        boolean changed = this.hasCustomEffects();
        this.customEffects = null;
        return changed;
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (this.hasCustomEffects()) {
            hash = 73 * hash + this.customEffects.hashCode();
        }
        return original != hash ? CraftMetaSuspiciousStew.class.hashCode() ^ hash : hash;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaSuspiciousStew that) {
            return (this.hasCustomEffects() ? that.hasCustomEffects() && this.customEffects.equals(that.customEffects) : !that.hasCustomEffects());
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaSuspiciousStew || this.isStewEmpty());
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);

        if (this.hasCustomEffects()) {
            builder.put(CraftMetaSuspiciousStew.EFFECTS.BUKKIT, ImmutableList.copyOf(this.customEffects));
        }

        return builder;
    }

	@Override
	public boolean addCustomEffect(SuspiciousEffectEntry suspiciousEffectEntry, boolean overwrite) {
        List<SuspiciousEffectEntry> matchingEffects;
        Preconditions.checkArgument((suspiciousEffectEntry != null ? 1 : 0) != 0, (Object)"Suspicious effect entry cannot be null");
        if (this.hasCustomEffects() && !(matchingEffects = this.customEffects.stream().filter(entry -> entry.effect() == suspiciousEffectEntry.effect()).toList()).isEmpty()) {
            if (overwrite) {
                boolean foundMatchingDuration = false;
                boolean mutated = false;
                for (SuspiciousEffectEntry matchingEffect : matchingEffects) {
                    if (matchingEffect.duration() != suspiciousEffectEntry.duration()) {
                        this.customEffects.remove(suspiciousEffectEntry);
                        mutated = true;
                        continue;
                    }
                    foundMatchingDuration = true;
                }
                if (foundMatchingDuration && !mutated) {
                    return false;
                }
                if (!foundMatchingDuration) {
                    this.customEffects.add(suspiciousEffectEntry);
                }
                return true;
            }
            return false;
        }
        if (this.customEffects == null) {
            this.customEffects = new ArrayList<SuspiciousEffectEntry>();
        }
        this.customEffects.add(suspiciousEffectEntry);
        return true;
    }
	
}

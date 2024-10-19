package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftMetaItem;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.inventory.meta.FireworkMeta;

@DelegateDeserialization(value=SerializableMeta.class)
public class CraftMetaFirework
extends CraftMetaItem
implements FireworkMeta {
    static final CraftMetaItem.ItemMetaKeyType<FireworksComponent> FIREWORKS = new CraftMetaItem.ItemMetaKeyType<FireworksComponent>(DataComponentTypes.FIREWORKS, "Fireworks");
    static final CraftMetaItem.ItemMetaKey FLIGHT = new CraftMetaItem.ItemMetaKey("power");
    static final CraftMetaItem.ItemMetaKey EXPLOSIONS = new CraftMetaItem.ItemMetaKey("firework-effects");
    private ArrayList<FireworkEffect> effects;
    public int power;

    CraftMetaFirework(CraftMetaItem meta) {
        super(meta);
        if (!(meta instanceof CraftMetaFirework)) {
            return;
        }
        CraftMetaFirework that = (CraftMetaFirework)meta;
        this.power = that.power;
        if (that.hasEffects()) {
            this.effects = new ArrayList<FireworkEffect>(that.effects);
        }
    }

    CraftMetaFirework(ComponentChanges tag, Set<ComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaFirework.getOrEmpty(tag, FIREWORKS).ifPresent(fireworks -> {
            this.power = fireworks.flightDuration();
            List<FireworkExplosionComponent> fireworkEffects = fireworks.explosions();
            this.effects = new ArrayList<FireworkEffect>(fireworkEffects.size());
            ArrayList<FireworkEffect> effects = this.effects;
            for (int i2 = 0; i2 < fireworkEffects.size(); ++i2) {
                try {
                    effects.add(CraftMetaFirework.getEffect(fireworkEffects.get(i2)));
                    continue;
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            }
        });
    }

    static FireworkEffect getEffect(FireworkExplosionComponent explosion) {
        int color;
        FireworkEffect.Builder effect = FireworkEffect.builder().flicker(explosion.hasTwinkle()).trail(explosion.hasTrail()).with(CraftMetaFirework.getEffectType(explosion.shape()));
        IntList colors = explosion.colors();
        if (colors.isEmpty()) {
            effect.withColor(Color.WHITE);
        }
        IntListIterator intListIterator = colors.iterator();
        while (intListIterator.hasNext()) {
            color = (Integer)intListIterator.next();
            effect.withColor(Color.fromRGB((int)color));
        }
        intListIterator = explosion.fadeColors().iterator();
        while (intListIterator.hasNext()) {
            color = (Integer)intListIterator.next();
            effect.withFade(Color.fromRGB((int)color));
        }
        return effect.build();
    }

    static FireworkExplosionComponent getExplosion(FireworkEffect effect) {
        IntList colors = CraftMetaFirework.addColors(effect.getColors());
        IntList fadeColors = CraftMetaFirework.addColors(effect.getFadeColors());
        return new FireworkExplosionComponent(CraftMetaFirework.getNBT(effect.getType()), colors, fadeColors, effect.hasTrail(), effect.hasFlicker());
    }

    public static FireworkExplosionComponent.Type getNBT(FireworkEffect.Type type) {
        switch (type) {
            case BALL: {
                return FireworkExplosionComponent.Type.SMALL_BALL;
            }
            case BALL_LARGE: {
                return FireworkExplosionComponent.Type.LARGE_BALL;
            }
            case STAR: {
                return FireworkExplosionComponent.Type.STAR;
            }
            case CREEPER: {
                return FireworkExplosionComponent.Type.CREEPER;
            }
            case BURST: {
                return FireworkExplosionComponent.Type.BURST;
            }
        }
        throw new IllegalArgumentException("Unknown effect type " + String.valueOf(type));
    }

    static FireworkEffect.Type getEffectType(FireworkExplosionComponent.Type nbt) {
        switch (nbt) {
            case SMALL_BALL: {
                return FireworkEffect.Type.BALL;
            }
            case LARGE_BALL: {
                return FireworkEffect.Type.BALL_LARGE;
            }
            case STAR: {
                return FireworkEffect.Type.STAR;
            }
            case CREEPER: {
                return FireworkEffect.Type.CREEPER;
            }
            case BURST: {
                return FireworkEffect.Type.BURST;
            }
        }
        throw new IllegalArgumentException("Unknown effect type " + String.valueOf(nbt));
    }

    CraftMetaFirework(Map<String, Object> map) {
        super(map);
        Integer power = org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getObject(Integer.class, map, CraftMetaFirework.FLIGHT.BUKKIT, true);
        if (power != null) {
            this.power = power;
        }
        Iterable effects = org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getObject(Iterable.class, map, CraftMetaFirework.EXPLOSIONS.BUKKIT, true);
        this.safelyAddEffects(effects, false);
    }

    public boolean hasEffects() {
        return this.effects != null && !this.effects.isEmpty();
    }

    void safelyAddEffects(Iterable<?> collection, boolean throwOnOversize) {
        if (collection == null || collection instanceof Collection && ((Collection)collection).isEmpty()) {
            return;
        }
        List<FireworkEffect> effects = this.effects;
        if (effects == null) {
            effects = this.effects = new ArrayList<FireworkEffect>();
        }
        for (Object obj : collection) {
            Preconditions.checkArgument((boolean)(obj instanceof FireworkEffect), (String)"%s in %s is not a FireworkEffect", obj, collection);
            if (effects.size() + 1 > 256) {
                if (!throwOnOversize) continue;
                throw new IllegalArgumentException("Cannot have more than 256 firework effects");
            }
            effects.add((FireworkEffect)obj);
        }
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator itemTag) {
        super.applyToItem(itemTag);
        if (this.isFireworkEmpty()) {
            return;
        }
        ArrayList<FireworkExplosionComponent> effects = new ArrayList<FireworkExplosionComponent>();
        if (this.effects != null) {
            for (FireworkEffect effect : this.effects) {
                effects.add(CraftMetaFirework.getExplosion(effect));
            }
        }
        itemTag.put(FIREWORKS, new FireworksComponent(this.power, effects));
    }

    static IntList addColors(List<Color> colors) {
        if (colors.isEmpty()) {
            return IntList.of();
        }
        int[] colorArray = new int[colors.size()];
        int i2 = 0;
        for (Color color : colors) {
            colorArray[i2++] = color.asRGB();
        }
        return IntList.of((int[])colorArray);
    }

    @Override
    boolean applicableTo(Material type) {
        return type == Material.FIREWORK_ROCKET;
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.isFireworkEmpty();
    }

    boolean isFireworkEmpty() {
        return !this.hasEffects() && !this.hasPower();
    }

    boolean hasPower() {
        return this.power != 0;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaFirework) {
            CraftMetaFirework that = (CraftMetaFirework)meta;
            return (this.hasPower() ? that.hasPower() && this.power == that.power : !that.hasPower()) && (this.hasEffects() ? that.hasEffects() && this.effects.equals(that.effects) : !that.hasEffects());
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaFirework || this.isFireworkEmpty());
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.hasPower()) {
            hash = 61 * hash + this.power;
        }
        if (this.hasEffects()) {
            hash = 61 * hash + 13 * this.effects.hashCode();
        }
        return hash != original ? CraftMetaFirework.class.hashCode() ^ hash : hash;
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        if (this.hasEffects()) {
            builder.put(CraftMetaFirework.EXPLOSIONS.BUKKIT, ImmutableList.copyOf(this.effects));
        }
        if (this.hasPower()) {
            builder.put(CraftMetaFirework.FLIGHT.BUKKIT, this.power);
        }
        return builder;
    }

    @Override
    public CraftMetaFirework clone() {
        CraftMetaFirework meta = (CraftMetaFirework)super.clone();
        if (this.effects != null) {
            meta.effects = new ArrayList<FireworkEffect>(this.effects);
        }
        return meta;
    }

    public void addEffect(FireworkEffect effect) {
        Preconditions.checkArgument((effect != null ? 1 : 0) != 0, "FireworkEffect cannot be null");
        Preconditions.checkArgument((this.effects == null || this.effects.size() + 1 <= 256 ? 1 : 0) != 0, (String)"cannot have more than %s firework effects", (int)256);
        if (this.effects == null) {
            this.effects = new ArrayList<FireworkEffect>();
        }
        this.effects.add(effect);
    }

    public void addEffects(FireworkEffect ... effects) {
        Preconditions.checkArgument((effects != null ? 1 : 0) != 0, "effects cannot be null");
        int initialSize = this.effects == null ? 0 : this.effects.size();
        Preconditions.checkArgument((initialSize + effects.length <= 256 ? 1 : 0) != 0, (String)"Cannot have more than %s firework effects", (int)256);
        if (effects.length == 0) {
            return;
        }
        List<FireworkEffect> list = this.effects;
        if (list == null) {
            list = this.effects = new ArrayList<FireworkEffect>();
        }
        for (FireworkEffect effect : effects) {
            Preconditions.checkArgument((effect != null ? 1 : 0) != 0, "effects cannot contain null FireworkEffect");
            list.add(effect);
        }
    }

    public void addEffects(Iterable<FireworkEffect> effects) {
        Preconditions.checkArgument((effects != null ? 1 : 0) != 0, "effects cannot be null");
        this.safelyAddEffects(effects, true);
    }

    public List<FireworkEffect> getEffects() {
        return this.effects == null ? ImmutableList.of() : ImmutableList.copyOf(this.effects);
    }

    public int getEffectsSize() {
        return this.effects == null ? 0 : this.effects.size();
    }

    public void removeEffect(int index) {
        if (this.effects == null) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
        }
        this.effects.remove(index);
    }

    public void clearEffects() {
        this.effects = null;
    }

    public int getPower() {
        return this.hasPower() ? this.power : 0;
    }

    public void setPower(int power) {
        Preconditions.checkArgument((power >= 0 ? 1 : 0) != 0, (String)"power cannot be less than zero: %s", (int)power);
        Preconditions.checkArgument((power < 128 ? 1 : 0) != 0, (String)"power cannot be more than 127: %s", (int)power);
        this.power = power;
    }
}


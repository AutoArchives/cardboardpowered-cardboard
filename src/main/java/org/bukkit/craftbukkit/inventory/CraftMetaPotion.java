package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftMetaItem;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.potion.CraftPotionType;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

@DelegateDeserialization(value=SerializableMeta.class)
class CraftMetaPotion
extends CraftMetaItem
implements PotionMeta {
    private static final Set<Material> POTION_MATERIALS = Sets.newHashSet(
    		new Material[]{Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION, Material.TIPPED_ARROW}
    	);
    static final CraftMetaItem.ItemMetaKeyType<PotionContentsComponent> POTION_CONTENTS = new CraftMetaItem.ItemMetaKeyType<PotionContentsComponent>(DataComponentTypes.POTION_CONTENTS);
    static final CraftMetaItem.ItemMetaKey POTION_EFFECTS = new CraftMetaItem.ItemMetaKey("custom-effects");
    static final CraftMetaItem.ItemMetaKey POTION_COLOR = new CraftMetaItem.ItemMetaKey("custom-color");
    static final CraftMetaItem.ItemMetaKey DEFAULT_POTION = new CraftMetaItem.ItemMetaKey("potion-type");
    private PotionType type;
    private List<PotionEffect> customEffects;
    private Color color;

    CraftMetaPotion(CraftMetaItem meta) {
        super(meta);
        if (!(meta instanceof CraftMetaPotion)) {
            return;
        }
        CraftMetaPotion potionMeta = (CraftMetaPotion)meta;
        this.type = potionMeta.type;
        this.color = potionMeta.color;
        if (potionMeta.hasCustomEffects()) {
            this.customEffects = new ArrayList<PotionEffect>(potionMeta.customEffects);
        }
    }

    CraftMetaPotion(ComponentChanges tag, Set<ComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaPotion.getOrEmpty(tag, POTION_CONTENTS).ifPresent(potionContents -> {
            potionContents.potion().ifPresent(potion -> {
                this.type = CraftPotionType.minecraftHolderToBukkit(potion);
            });
            potionContents.customColor().ifPresent(customColor -> {
                try {
                    this.color = Color.fromRGB((int)customColor);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            });
            List<StatusEffectInstance> list = potionContents.customEffects();
            int length = list.size();
            this.customEffects = new ArrayList<PotionEffect>(length);
            for (int i2 = 0; i2 < length; ++i2) {
                StatusEffectInstance effect = list.get(i2);
                PotionEffectType type = CraftPotionEffectType.minecraftHolderToBukkit(effect.getEffectType());
                if (type == null) continue;
                int amp = effect.getAmplifier();
                int duration = effect.getDuration();
                boolean ambient = effect.isAmbient();
                boolean particles = effect.shouldShowParticles();
                boolean icon = effect.shouldShowIcon();
                this.customEffects.add(new PotionEffect(type, duration, amp, ambient, particles, icon));
            }
        });
    }

    CraftMetaPotion(Map<String, Object> map) {
        super(map);
        Iterable rawEffectList;
        Color color;
        String typeString = org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getString(map, CraftMetaPotion.DEFAULT_POTION.BUKKIT, true);
        if (typeString != null) {
            this.type = CraftPotionType.stringToBukkit(typeString);
        }
        if ((color = org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getObject(Color.class, map, CraftMetaPotion.POTION_COLOR.BUKKIT, true)) != null) {
            this.setColor(color);
        }
        if ((rawEffectList = org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getObject(Iterable.class, map, CraftMetaPotion.POTION_EFFECTS.BUKKIT, true)) == null) {
            return;
        }
        for (Object obj : rawEffectList) {
            Preconditions.checkArgument((boolean)(obj instanceof PotionEffect), (String)"Object (%s) in effect list is not valid", obj.getClass());
            this.addCustomEffect((PotionEffect)obj, true);
        }
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator tag) {
        super.applyToItem(tag);
        Optional<RegistryEntry<Potion>> defaultPotion = this.hasBasePotionType() ? Optional.of(CraftPotionType.bukkitToMinecraftHolder(this.type)) : Optional.empty();
        Optional<Integer> potionColor = this.hasColor() ? Optional.of(this.color.asRGB()) : Optional.empty();
        ArrayList<StatusEffectInstance> effectList = new ArrayList<StatusEffectInstance>();
        if (this.customEffects != null) {
            for (PotionEffect effect : this.customEffects) {
                effectList.add(new StatusEffectInstance(CraftPotionEffectType.bukkitToMinecraftHolder(effect.getType()), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon()));
            }
        }
        tag.put(POTION_CONTENTS, new PotionContentsComponent(defaultPotion, potionColor, effectList));
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.isPotionEmpty();
    }

    boolean isPotionEmpty() {
        return this.type == null && !this.hasCustomEffects() && !this.hasColor();
    }

    @Override
    boolean applicableTo(Material type) {
        return POTION_MATERIALS.contains(type);
    }

    @Override
    public CraftMetaPotion clone() {
        CraftMetaPotion clone = (CraftMetaPotion)super.clone();
        clone.type = this.type;
        if (this.customEffects != null) {
            clone.customEffects = new ArrayList<PotionEffect>(this.customEffects);
        }
        return clone;
    }

    public void setBasePotionType(PotionType potionType) {
        this.type = potionType;
    }

    public PotionType getBasePotionType() {
        return this.type;
    }

    public boolean hasBasePotionType() {
        return this.type != null;
    }

    public boolean hasCustomEffects() {
        return this.customEffects != null;
    }

    public List<PotionEffect> getCustomEffects() {
        if (this.hasCustomEffects()) {
            return ImmutableList.copyOf(this.customEffects);
        }
        return ImmutableList.of();
    }

    public boolean addCustomEffect(PotionEffect effect, boolean overwrite) {
        Preconditions.checkArgument((effect != null ? 1 : 0) != 0, "Potion effect cannot be null");
        int index = this.indexOfEffect(effect.getType());
        if (index != -1) {
            if (overwrite) {
                PotionEffect old = this.customEffects.get(index);
                if (old.getAmplifier() == effect.getAmplifier() && old.getDuration() == effect.getDuration() && old.isAmbient() == effect.isAmbient()) {
                    return false;
                }
                this.customEffects.set(index, effect);
                return true;
            }
            return false;
        }
        if (this.customEffects == null) {
            this.customEffects = new ArrayList<PotionEffect>();
        }
        this.customEffects.add(effect);
        return true;
    }

    public boolean removeCustomEffect(PotionEffectType type) {
        Preconditions.checkArgument((type != null ? 1 : 0) != 0, "Potion effect type cannot be null");
        if (!this.hasCustomEffects()) {
            return false;
        }
        boolean changed = false;
        Iterator<PotionEffect> iterator = this.customEffects.iterator();
        while (iterator.hasNext()) {
            PotionEffect effect = iterator.next();
            if (!type.equals(effect.getType())) continue;
            iterator.remove();
            changed = true;
        }
        if (this.customEffects.isEmpty()) {
            this.customEffects = null;
        }
        return changed;
    }

    public boolean hasCustomEffect(PotionEffectType type) {
        Preconditions.checkArgument((type != null ? 1 : 0) != 0, "Potion effect type cannot be null");
        return this.indexOfEffect(type) != -1;
    }

    public boolean setMainEffect(PotionEffectType type) {
        Preconditions.checkArgument((type != null ? 1 : 0) != 0, "Potion effect type cannot be null");
        int index = this.indexOfEffect(type);
        if (index == -1 || index == 0) {
            return false;
        }
        PotionEffect old = this.customEffects.get(0);
        this.customEffects.set(0, this.customEffects.get(index));
        this.customEffects.set(index, old);
        return true;
    }

    private int indexOfEffect(PotionEffectType type) {
        if (!this.hasCustomEffects()) {
            return -1;
        }
        for (int i2 = 0; i2 < this.customEffects.size(); ++i2) {
            if (!this.customEffects.get(i2).getType().equals(type)) continue;
            return i2;
        }
        return -1;
    }

    public boolean clearCustomEffects() {
        boolean changed = this.hasCustomEffects();
        this.customEffects = null;
        return changed;
    }

    public boolean hasColor() {
        return this.color != null;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.type != null) {
            hash = 73 * hash + this.type.hashCode();
        }
        if (this.hasColor()) {
            hash = 73 * hash + this.color.hashCode();
        }
        if (this.hasCustomEffects()) {
            hash = 73 * hash + this.customEffects.hashCode();
        }
        return original != hash ? CraftMetaPotion.class.hashCode() ^ hash : hash;
    }

    @Override
    public boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaPotion) {
            CraftMetaPotion that = (CraftMetaPotion)meta;
            return Objects.equals(this.type, that.type) && (this.hasCustomEffects() ? that.hasCustomEffects() && this.customEffects.equals(that.customEffects) : !that.hasCustomEffects()) && (this.hasColor() ? that.hasColor() && this.color.equals(that.color) : !that.hasColor());
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaPotion || this.isPotionEmpty());
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        if (this.type != null) {
            builder.put(CraftMetaPotion.DEFAULT_POTION.BUKKIT, CraftPotionType.bukkitToString(this.type));
        }
        if (this.hasColor()) {
            builder.put(CraftMetaPotion.POTION_COLOR.BUKKIT, this.getColor());
        }
        if (this.hasCustomEffects()) {
            builder.put(CraftMetaPotion.POTION_EFFECTS.BUKKIT, ImmutableList.copyOf(this.customEffects));
        }
        return builder;
    }

	@Override
	public @NotNull PotionData getBasePotionData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBasePotionData(@NotNull PotionData arg0) {
		// TODO Auto-generated method stub
		
	}
}


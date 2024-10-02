package org.cardboardpowered.impl;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntry.Reference;

import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.potion.CraftPotionEffectTypeCategory;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class CardboardPotionEffectType extends PotionEffectType implements Handleable<StatusEffect> {

	public static RegistryEntry<StatusEffect> bukkitToMinecraftHolder(PotionEffectType type) {
		// TODO Auto-generated method stub
		// return CraftRegistry.bukkitToMinecraftHolder(..);

		Optional<Reference<StatusEffect>> opt = Registries.STATUS_EFFECT.getEntry(type.getId());
		
		if (opt.isPresent()) {
			return opt.get();
		}
		return null;
	}
	
    public static PotionEffectType minecraftHolderToBukkit(RegistryEntry<StatusEffect> minecraft) {
        return CraftPotionEffectType.minecraftToBukkit(minecraft.value());
    }

	
    public static PotionEffectType minecraftToBukkit(StatusEffect minecraft) {
        return (PotionEffectType)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.STATUS_EFFECT, Registry.EFFECT);
    }
	
	
    private final NamespacedKey key;
    private final StatusEffect handle;
    private final int id;
    
    
    public CardboardPotionEffectType(NamespacedKey key, StatusEffect handle) {
    	// super(Registries.STATUS_EFFECT.getRawId(handle), CraftNamespacedKey.fromMinecraft(Registries.STATUS_EFFECT.getId(handle)));
    	this.key = key;
        this.handle = handle;
        
        // RegistryKeys.STATUS_EFFECT
        
        // this.id = Registries.STATUS_EFFECT.getRawId(handle) + 1;
        
        this.id = CraftRegistry.getMinecraftRegistry(RegistryKeys.STATUS_EFFECT).getRawId(handle) + 1;
    }

    @Deprecated
    public CardboardPotionEffectType(StatusEffect handle) {
        this(CraftNamespacedKey.fromMinecraft(Registries.STATUS_EFFECT.getId(handle)), handle);
        
    }


    @Override
    public double getDurationModifier() {
        return 1.0D;
    }

    public StatusEffect getHandle() {
        return handle;
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getName() {
        switch (getId()) {
            case 1:
                return "SPEED";
            case 2:
                return "SLOW";
            case 3:
                return "FAST_DIGGING";
            case 4:
                return "SLOW_DIGGING";
            case 5:
                return "INCREASE_DAMAGE";
            case 6:
                return "HEAL";
            case 7:
                return "HARM";
            case 8:
                return "JUMP";
            case 9:
                return "CONFUSION";
            case 10:
                return "REGENERATION";
            case 11:
                return "DAMAGE_RESISTANCE";
            case 12:
                return "FIRE_RESISTANCE";
            case 13:
                return "WATER_BREATHING";
            case 14:
                return "INVISIBILITY";
            case 15:
                return "BLINDNESS";
            case 16:
                return "NIGHT_VISION";
            case 17:
                return "HUNGER";
            case 18:
                return "WEAKNESS";
            case 19:
                return "POISON";
            case 20:
                return "WITHER";
            case 21:
                return "HEALTH_BOOST";
            case 22:
                return "ABSORPTION";
            case 23:
                return "SATURATION";
            case 24:
                return "GLOWING";
            case 25:
                return "LEVITATION";
            case 26:
                return "LUCK";
            case 27:
                return "UNLUCK";
            case 28:
                return "SLOW_FALLING";
            case 29:
                return "CONDUIT_POWER";
            case 30:
                return "DOLPHINS_GRACE";
            case 31:
                return "BAD_OMEN";
            case 32:
                return "HERO_OF_THE_VILLAGE";
            default:
                return "UNKNOWN_EFFECT_TYPE_" + getId();
        }
    }

    @Override
    public boolean isInstant() {
        return handle.isInstant();
    }

    @Override
    public Color getColor() {
        return Color.fromRGB(handle.getColor());
    }

	@Override
	public @NotNull String translationKey() {
		return this.getHandle().getTranslationKey();
	}

	@Override
	public double getAttributeModifierAmount(@NotNull Attribute arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public @NotNull Map<Attribute, AttributeModifier> getEffectAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull Category getEffectCategory() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// 1.20.3 API:

	@Override
	public NamespacedKey getKey() {
		return this.key;
	}

	@Override
	public @NotNull PotionEffect createEffect(int duration, int amplifier) {
        return new PotionEffect(this, this.isInstant() ? 1 : (int) (duration * this.getDurationModifier()), amplifier);
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public @NotNull String getTranslationKey() {
		// TODO Auto-generated method stub
		return this.key.toString();
	}

	@Override
	public @NotNull PotionEffectTypeCategory getCategory() {
		return CraftPotionEffectTypeCategory.minecraftToBukkit(this.handle.getCategory());
	}

}

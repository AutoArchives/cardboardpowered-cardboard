package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;

import io.papermc.paper.potion.SuspiciousEffectEntry;
import net.kyori.adventure.sound.Sound.Source;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.List;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class CraftMushroomCow extends CraftAbstractCow implements MushroomCow {

    public CraftMushroomCow(CraftServer server, net.minecraft.world.entity.animal.cow.MushroomCow entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.cow.MushroomCow getHandle() {
        return (net.minecraft.world.entity.animal.cow.MushroomCow) entity;
    }

    @Override
    public Variant getVariant() {
        return Variant.values()[getHandle().getVariant().ordinal()];
    }

    @Override
    public void setVariant(Variant variant) {
        Preconditions.checkArgument(variant != null, "variant");
        // getHandle().setVariant(MooshroomEntity.Type.values()[variant.ordinal()]);
        this.getHandle().setVariant(net.minecraft.world.entity.animal.cow.MushroomCow.Variant.values()[variant.ordinal()]);

    }

    @Override
    public String toString() {
        return "MushroomCow";
    }

    @Override
    public EntityType getType() {
        return EntityType.MOOSHROOM;
    }

	@Override
	public int getStewEffectDuration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
    public PotionEffectType getStewEffectType() {
        // StatusEffect effect = this.getHandle().stewEffect;
        // if (effect == null) {
       //      return null;
        // }
        return null; // PotionEffectType.getById((int)StatusEffect.getRawId(effect));
    }

	@Override
	public void setStewEffect(@Nullable PotionEffectType arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStewEffectDuration(int arg0) {
		// TODO Auto-generated method stub
		//this.getHandle().stewEffectDuration = duration;
	}

	@Override
	public boolean readyToBeSheared() {
		return this.getHandle().readyForShearing();
	}

	@Override
	public void shear(@NotNull Source arg0) {
		// this.getHandle().sheared(net.minecraft.sound.SoundCategory.AMBIENT);

		if (!(this.getHandle().level() instanceof final ServerLevel serverLevel)) return;
        this.getHandle().shear(serverLevel, CardboardAdventure.asVanilla(arg0), new ItemStack(Items.SHEARS));
	}
	
	// 1.20.2 API:

	@Override
	public @NotNull @Unmodifiable List<SuspiciousEffectEntry> getStewEffects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStewEffects(@NotNull List<SuspiciousEffectEntry> effects) {
		// TODO Auto-generated method stub
		/*
		if (effects.isEmpty()) {
            this.getHandle().stewEffects = null;
            return;
        }
        ArrayList<SuspiciousStewEffectsComponent.StewEffect> nmsPairs = new ArrayList<SuspiciousStewEffectsComponent.StewEffect>(effects.size());
        for (SuspiciousEffectEntry effect : effects) {
            nmsPairs.add(new SuspiciousStewEffectsComponent.StewEffect(CraftPotionEffectType.bukkitToMinecraftHolder(effect.effect()), effect.duration()));
        }
        this.getHandle().stewEffects = new SuspiciousStewEffectsComponent(nmsPairs);
        */
	}
	
	// 1.20.4 API:

	@Override
	public boolean hasEffectsForNextStew() {
        // SuspiciousStewEffectsComponent stewEffects = this.getHandle().stewEffects;
        //return stewEffects != null && !stewEffects.effects().isEmpty();
		return false;
	}

	@Override
	public @NotNull List<PotionEffect> getEffectsForNextStew() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addEffectToNextStew(@NotNull PotionEffect effect, boolean overwrite) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addEffectToNextStew(@NotNull SuspiciousEffectEntry suspiciousEffectEntry, boolean overwrite) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeEffectFromNextStew(@NotNull PotionEffectType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasEffectForNextStew(@NotNull PotionEffectType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clearEffectsForNextStew() {
		// TODO Auto-generated method stub
		
	}

}
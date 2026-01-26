package org.cardboardpowered.impl.entity;

import net.kyori.adventure.text.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftProjectile;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CardboardFishHook extends CraftProjectile implements FishHook {

    private double biteChance = -1;

    public CardboardFishHook(CraftServer server, FishingHook entity) {
        super(server, entity);
    }

    @Override
    public FishingHook getHandle() {
        return (FishingHook) entity;
    }

    @Override
    public String toString() {
        return "CardboardFishingHook";
    }

    @Override
    public EntityType getType() {
        return EntityType.FISHING_BOBBER;
    }

    @Override
    public double getBiteChance() {
        FishingHook hook = getHandle();
        if (this.biteChance == -1) {
            if (hook.level().isRainingAt(new BlockPos(Mth.floor(hook.getX()), Mth.floor(hook.getY()) + 1, Mth.floor(hook.getZ()))))
                return 1 / 300.0;
            return 1 / 500.0;
        }
        return this.biteChance;
    }

    @Override
    public void setBiteChance(double chance) {
        Validate.isTrue(chance >= 0 && chance <= 1, "The bite chance must be between 0 and 1.");
        this.biteChance = chance;
    }

    @Override
    public boolean getApplyLure() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getMaxWaitTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMinWaitTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setApplyLure(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setMaxWaitTime(int arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setMinWaitTime(int arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public @Nullable Component customName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void customName(@Nullable Component arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public @Nullable Entity getHookedEntity() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public @NotNull HookState getState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isInOpenWater() {
        // TODO Auto-generated method stub
        return entity.isInWater();
    }

    @Override
    public boolean pullHookedEntity() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setHookedEntity(@Nullable Entity arg0) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public int getWaitTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setWaitTime(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	// 1.19.4:

	@Override
	public float getMaxLureAngle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxLureTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getMinLureAngle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinLureTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isRainInfluenced() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSkyInfluenced() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setLureAngle(float arg0, float arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLureTime(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMaxLureAngle(float arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMaxLureTime(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMinLureAngle(float arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMinLureTime(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRainInfluenced(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSkyInfluenced(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWaitTime(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	// 1.20.6 API
	
	@Override
	public int getTimeUntilBite() {
		// TODO Auto-generated method stub
		// return this.getHandle().fishTravelCountdown;
		return 0;
	}

	@Override
	public void setTimeUntilBite(int ticks) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetFishingState() {
        FishingHook hook = this.getHandle();
        // hook.resetTimeUntilLured();
        // hook.fishTravelCountdown = 0;
	}

	@Override
	public int retrieve(EquipmentSlot slot) {
        FishingHook fishingHook = this.getHandle();
        Player playerOwner = fishingHook.getPlayerOwner();
        InteractionHand hand = CraftEquipmentSlot.getHand(slot);
        ItemStack itemInHand = playerOwner.getItemInHand(hand);
        return fishingHook.retrieve(itemInHand); // .retrieve(itemInHand, hand);
    }

}

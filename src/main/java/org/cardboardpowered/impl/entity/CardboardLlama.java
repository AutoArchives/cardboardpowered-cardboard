package org.cardboardpowered.impl.entity;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.entity.CraftChestedHorse;
import org.cardboardpowered.bridge.world.entity.EntityBridge;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.inventory.LlamaInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CardboardLlama extends CraftChestedHorse implements Llama {

    public CardboardLlama(CraftServer server, net.minecraft.world.entity.animal.equine.Llama entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.equine.Llama getHandle() {
        return (net.minecraft.world.entity.animal.equine.Llama)super.getHandle();
    }

    @Override
    public Llama.Color getColor() {
        return Llama.Color.values()[this.getHandle().getVariant().getId()];
    }

    @Override
    public void setColor(Llama.Color color) {
        Preconditions.checkArgument(color != null, "color");
        this.getHandle().setVariant(net.minecraft.world.entity.animal.equine.Llama.Variant.byId(color.ordinal()));
    }

    @Override
    public LlamaInventory getInventory() {
        return null;//new CardboardInventoryLlama(this.getHandle().items);
    }

    @Override
    public int getStrength() {
        return this.getHandle().getStrength();
    }

    @Override
    public void setStrength(int strength) {
        Preconditions.checkArgument(1 <= strength && strength <= 5, "strength must be [1,5]");
        if (strength == this.getStrength()) {
            return;
        }
        //this.getHandle().setStrength(strength);
        //this.getHandle().onChestedStatusChanged();
    }

    @Override
    public Horse.Variant getVariant() {
        return Horse.Variant.LLAMA;
    }

    @Override
    public String toString() {
        return "Llama";
    }

    @Override
    public EntityType getType() {
        return EntityType.LLAMA;
    }

    @Override
    public void rangedAttack(LivingEntity arg0, float arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setChargingAttack(boolean bl) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public boolean isEatingHaystack() {
		return this.getHandle().isEating();
	}

	@Override
	public void setEatingHaystack(boolean arg0) {
		this.getHandle().setEating(arg0);
	}

	@Override
    public Llama getCaravanHead() {
        return this.getHandle().getCaravanHead() == null ? null : (Llama)((EntityBridge)this.getHandle().getCaravanHead()).getBukkitEntity();
    }

	@Override
	public @Nullable Llama getCaravanTail() {
		// return this.getHandle().follower == null ? null : (Llama)this.getHandle().follower.getBukkitEntity();
		return null;
	}

	@Override
	public boolean hasCaravanTail() {
        return this.getHandle().hasCaravanTail();
	}

	@Override
	public boolean inCaravan() {
        return this.getHandle().inCaravan();
	}

	@Override
	public void joinCaravan(@NotNull Llama arg0) {
		this.getHandle().joinCaravan(((CardboardLlama)arg0).getHandle());
	}

	@Override
	public void leaveCaravan() {
		this.getHandle().leaveCaravan();
	}

}

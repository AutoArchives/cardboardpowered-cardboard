package org.cardboardpowered.impl.entity;

import net.kyori.adventure.util.TriState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.item.ItemEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.cardboardpowered.interfaces.CardboardItemEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ItemEntityImpl extends CraftEntity implements Item {

    private final ItemEntity item;

    public ItemEntityImpl(CraftServer server, Entity entity, ItemEntity item) {
        super(entity);
        this.item = item;
    }

    public ItemEntityImpl(CraftServer server, ItemEntity entity) {
        this(server, entity, entity);
    }
    
    public ItemEntity getHandle() {
    	return (ItemEntity) this.entity;
    }

    @Override
    public ItemStack getItemStack() {
        return CraftItemStack.asCraftMirror(item.getItem());
    }

    @Override
    public void setItemStack(ItemStack stack) {
        item.setItem(CraftItemStack.asNMSCopy(stack));
    }

    @Override
    public int getPickupDelay() {
        return item.pickupDelay;
    }

    @Override
    public void setPickupDelay(int delay) {
        item.pickupDelay = Math.min(delay, Short.MAX_VALUE);
    }

    @Override
    public void setTicksLived(int value) {
        super.setTicksLived(value);
     // TODO 1.17ify item.itemAge = value;
    }

    @Override
    public String toString() {
        return "CraftItem";
    }

    @Override
    public EntityType getType() {
        return EntityType.ITEM;
    }

    public void setOwner(UUID uuid) {
        item.setTarget(uuid);
    }


    // Spigot #758
    public UUID getOwner() {
        return item.getOwner().getUUID();
    }

    // Spigot #758
    public void setThrower(UUID uuid) {
        item.thrower = uuid != null ? new EntityReference<Entity>(uuid) : null;;
    }

    // Spigot #758
    public UUID getThrower() {
    	EntityReference<Entity> thrower = item.thrower;
    	return thrower != null ? thrower.getUUID() : null;
    }

    @Override
    public boolean canMobPickup() {
        return !item.hasPickUpDelay();
    }

    @Override
    public boolean canPlayerPickup() {
        return !item.hasPickUpDelay();
    }

    @Override
    public void setCanMobPickup(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setCanPlayerPickup(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setWillAge(boolean willAge) {
        // TODO Auto-generated method stub
    	( (CardboardItemEntity) this.getHandle() ).cardboard$setItemAge( willAge ? 0 : -32768 );
    }

    @Override
    public boolean willAge() {
        // TODO Auto-generated method stub
        return this.getHandle().getAge() != -32768;
    }

	@Override
	public int getHealth() {
		return ( (CardboardItemEntity) this.getHandle() ).cardboard$getHealth();
	}

	@Override
	public boolean isUnlimitedLifetime() {
		return this.getHandle().getAge() == -32768;
	}

	@Override
	public void setHealth(int health) {
		( (CardboardItemEntity) this.getHandle() ).cardboard$setHealth(health);
	}

	@Override
	public void setUnlimitedLifetime(boolean arg0) {
		( (CardboardItemEntity) this.getHandle() ).cardboard$setUnlimitedAge(arg0);
	}

	@Override
	public @NotNull TriState getFrictionState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFrictionState(@NotNull TriState arg0) {
		// TODO Auto-generated method stub
		
	}

}
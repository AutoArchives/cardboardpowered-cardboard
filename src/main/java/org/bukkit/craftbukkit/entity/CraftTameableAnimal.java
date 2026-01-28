package org.bukkit.craftbukkit.entity;

import java.util.UUID;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Tameable;

import me.isaiah.common.cmixin.IMixinTameableEntity;
import net.minecraft.Optionull;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

public class CraftTameableAnimal extends CraftAnimals implements Tameable, Creature {

    public CraftTameableAnimal(CraftServer server, TamableAnimal entity) {
        super(server, entity);
    }

    @Override
    public TamableAnimal getHandle() {
        return (TamableAnimal) super.getHandle();
    }

    public UUID getOwnerUUID() {
    	return Optionull.map(this.getHandle().getOwnerReference(), EntityReference::getUUID);
    }

    public void setOwnerUUID(UUID uuid) {

        this.getHandle().setOwnerReference( (EntityReference)  (uuid == null ? null : new EntityReference<LivingEntity>(uuid)) );
    }

    public UUID getOwnerUniqueId() {return getOwnerUUID();} // Paper

    @Override
    public AnimalTamer getOwner() {
        if (getOwnerUUID() == null)
            return null;

        AnimalTamer owner = getServer().getPlayer(getOwnerUUID());
        if (owner == null)
            owner = getServer().getOfflinePlayer(getOwnerUUID());
        return owner;
    }

    @Override
    public boolean isTamed() {
        return getHandle().isTame();
    }

    @Override
    public void setOwner(AnimalTamer tamer) {
        if (tamer != null) {
            setTamed(true);
            // TODO getHandle().setGoalTarget(null, null, false);
            setOwnerUUID(tamer.getUniqueId());
        } else {
            setTamed(false);
            setOwnerUUID(null);
        }
    }

    @Override
    public void setTamed(boolean tame) {
    	((IMixinTameableEntity)getHandle()).IC$set_tamed(tame, true);    	
        // getHandle().setTamed(tame);
        if (!tame) setOwnerUUID(null);
    }

    public boolean isSitting() {
        return getHandle().isInSittingPose();
    }

    public void setSitting(boolean sitting) {
        getHandle().setInSittingPose(sitting);
        getHandle().setOrderedToSit(sitting);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{owner=" + getOwner() + ",tamed=" + isTamed() + "}";
    }

}
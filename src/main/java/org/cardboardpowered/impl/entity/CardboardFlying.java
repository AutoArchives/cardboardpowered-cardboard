package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;

import net.minecraft.entity.mob.MobEntity;

@Deprecated
public class CardboardFlying extends CraftMob {

	public CardboardFlying(CraftServer server, MobEntity entity) {
		super(server, entity);
	}
	
	/*
    public CardboardFlying(CraftServer server, FlyingEntity entity) {
        super(server, entity);
    }

    @Override
    public FlyingEntity getHandle() {
        return (FlyingEntity) nms;
    }

    @Override
    public String toString() {
        return "FlyingEntity";
    }
    */

}
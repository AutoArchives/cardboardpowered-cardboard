package org.cardboardpowered.impl.entity;

import net.minecraft.world.entity.Mob;
import org.bukkit.craftbukkit.CraftServer;

@Deprecated
public class CardboardFlying extends CraftMob {

	public CardboardFlying(CraftServer server, Mob entity) {
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
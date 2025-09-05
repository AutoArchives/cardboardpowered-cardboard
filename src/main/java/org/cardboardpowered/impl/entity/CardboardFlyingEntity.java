package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import net.minecraft.entity.mob.MobEntity;

/**
 * @deprecated FlyingEntity removed in 1.21.8 API
 */
@Deprecated
public class CardboardFlyingEntity extends CraftMob {

	public CardboardFlyingEntity(CraftServer server, MobEntity entity) {
		super(server, entity);
		// TODO Auto-generated constructor stub
	}

	/*
    public CardboardFlyingEntity(CraftServer server, FlyingEntity entity) {
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
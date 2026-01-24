package org.cardboardpowered.impl.entity;

import net.minecraft.world.entity.Mob;
import org.bukkit.craftbukkit.CraftServer;

/**
 * @deprecated FlyingEntity removed in 1.21.8 API
 */
@Deprecated
public class CardboardFlyingEntity extends CraftMob {

	public CardboardFlyingEntity(CraftServer server, Mob entity) {
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
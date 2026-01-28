package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;

public class CardboardGhast extends CraftMob implements Ghast {

    public CardboardGhast(CraftServer server, net.minecraft.world.entity.monster.Ghast entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.Ghast getHandle() {
        return (net.minecraft.world.entity.monster.Ghast) entity;
    }

    @Override
    public String toString() {
        return "Ghast";
    }

    @Override
    public EntityType getType() {
        return EntityType.GHAST;
    }

	@Override
	public int getExplosionPower() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isCharging() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCharging(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setExplosionPower(int arg0) {
		// TODO Auto-generated method stub
		
	}

}
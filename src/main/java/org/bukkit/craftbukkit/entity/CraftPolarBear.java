package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.PolarBearEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PolarBear;
import org.cardboardpowered.impl.entity.AnimalsImpl;

public class CraftPolarBear extends AnimalsImpl implements PolarBear {

    public CraftPolarBear(CraftServer server, PolarBearEntity entity) {
        super(server, entity);
    }

    @Override
    public PolarBearEntity getHandle() {
        return (PolarBearEntity) nms;
    }

    @Override
    public String toString() {
        return "FabricPolarBear";
    }

    @Override
    public EntityType getType() {
        return EntityType.POLAR_BEAR;
    }

	@Override
	public boolean isStanding() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setStanding(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

}
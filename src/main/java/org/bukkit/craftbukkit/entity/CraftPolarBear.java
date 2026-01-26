package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PolarBear;
import org.cardboardpowered.impl.entity.CraftAnimals;

public class CraftPolarBear extends CraftAnimals implements PolarBear {

    public CraftPolarBear(CraftServer server, net.minecraft.world.entity.animal.polarbear.PolarBear entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.polarbear.PolarBear getHandle() {
        return (net.minecraft.world.entity.animal.polarbear.PolarBear) entity;
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
package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftChestedHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Mule;

public class CardboardMule extends CraftChestedHorse implements Mule {

    public CardboardMule(CraftServer server, net.minecraft.world.entity.animal.equine.Mule entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "Mule";
    }

    @Override
    public EntityType getType() {
        return EntityType.MULE;
    }

    @Override
    public Horse.Variant getVariant() {
        return Horse.Variant.MULE;
    }

	@Override
	public boolean isEatingHaystack() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setEatingHaystack(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

}
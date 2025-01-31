package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.mob.IllagerEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Illager;
import org.cardboardpowered.impl.entity.CardboardRaider;

public class CraftIllager extends CardboardRaider implements Illager {

    public CraftIllager(CraftServer server, IllagerEntity entity) {
        super(server, entity);
    }

    @Override
    public IllagerEntity getHandle() {
        return (IllagerEntity) super.getHandle();
    }

    @Override
    public String toString() {
        return "Illager";
    }

	@Override
	public boolean isCelebrating() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCelebrating(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

}
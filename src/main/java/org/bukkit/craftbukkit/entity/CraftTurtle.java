package org.bukkit.craftbukkit.entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Turtle;
import org.cardboardpowered.impl.entity.CraftAnimals;

public class CraftTurtle extends CraftAnimals implements Turtle {

    public CraftTurtle(CraftServer server, net.minecraft.world.entity.animal.turtle.Turtle entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.turtle.Turtle getHandle() {
        return (net.minecraft.world.entity.animal.turtle.Turtle) super.getHandle();
    }

    @Override
    public String toString() {
        return "FabricTurtle";
    }

    @Override
    public EntityType getType() {
        return EntityType.TURTLE;
    }

    @Override
    public Location getHome() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasEgg() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDigging() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isGoingHome() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setHasEgg(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setHome(Location arg0) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public boolean isLayingEgg() {
		// TODO Auto-generated method stub
		return false;
	}

}
package org.cardboardpowered.impl.entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public class CardboardBat extends CardboardAmbient implements Bat {

    public CardboardBat(CraftServer server, net.minecraft.world.entity.ambient.Bat entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.ambient.Bat getHandle() {
        return (net.minecraft.world.entity.ambient.Bat) nms;
    }

    @Override
    public String toString() {
        return "Batman";
    }

    @Override
    public EntityType getType() {
        return EntityType.BAT;
    }

    @Override
    public boolean isAwake() {
        return !getHandle().isResting();
    }

    @Override
    public void setAwake(boolean state) {
        getHandle().setResting(!state);
    }

	@Override
	public @Nullable Location getTargetLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTargetLocation(@Nullable Location arg0) {
		// TODO Auto-generated method stub
		
	}

}
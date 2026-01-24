package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.ExplosiveMinecart;

import net.kyori.adventure.util.TriState;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;

public class CardboardTntCart extends CardboardMinecart implements ExplosiveMinecart {

    public CardboardTntCart(CraftServer server, AbstractMinecart entity) {
        super(server, entity);
    }

    @Override
    public EntityType getType() {
        return EntityType.TNT_MINECART;
    }

	@Override
    public int getFuseTicks() {
        return this.getHandle().getRemainingFireTicks();
    }

	@Override
	public void setFuseTicks(int arg0) {
        this.getHandle().remainingFireTicks = arg0;
	}
	
	// 1.19.4:
	
    @Override
    public MinecartTNT getHandle() {
        return (MinecartTNT)super.getHandle();
    }

	@Override
	public void explode() {
        this.getHandle().explode(null, this.getHandle().getDeltaMovement().horizontalDistanceSqr());
	}

	@Override
	public void explode(double arg0) {
        this.getHandle().explode(null, arg0);
	}

	@Override
	public void ignite() {
        this.getHandle().primeFuse(null);
	}

	@Override
	public boolean isIgnited() {
        return this.getHandle().isPrimed();
	}

	@Override
	public TriState getFrictionState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFrictionState(TriState state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setYield(float yield) {
		this.getHandle().explosionPowerBase = yield;
	}

	@Override
	public float getYield() {
		return this.getHandle().explosionPowerBase;
	}

	@Override
    public boolean isIncendiary() {
        return false; // TODO
		// return this.getHandle().isIncendiary;
    }

    @Override
    public void setIsIncendiary(boolean isIncendiary) {
        // this.getHandle().isIncendiary = isIncendiary;
    }

	@Override
	public float getExplosionSpeedFactor() {
		return this.getHandle().explosionSpeedFactor;
	}

	@Override
	public void setExplosionSpeedFactor(float factor) {
		this.getHandle().explosionSpeedFactor = factor;
	}

}
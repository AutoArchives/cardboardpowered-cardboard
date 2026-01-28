package org.cardboardpowered.impl.entity;

import com.google.common.base.Preconditions;
import org.cardboardpowered.bridge.world.entity.monster.CreeperBridge;
import org.cardboardpowered.bridge.world.entity.EntityBridge;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreeperImpl extends CraftMonster implements Creeper {

    public CreeperImpl(CraftServer server, net.minecraft.world.entity.monster.Creeper entity) {
        super(server, entity);
    }

    @Override
    public boolean isPowered() {
        return ((CreeperBridge)getHandle()).isPoweredBF();
    }

    @Override
    public void setPowered(boolean powered) {
        CraftServer server = this.server;
        Creeper entity = (Creeper) ((EntityBridge)this.getHandle()).getBukkitEntity();

        if (powered) {
            CreeperPowerEvent event = new CreeperPowerEvent(entity, CreeperPowerEvent.PowerCause.SET_ON);
            server.getPluginManager().callEvent(event);

            if (!event.isCancelled())
                ((CreeperBridge)getHandle()).setPowered(true);
        } else {
            CreeperPowerEvent event = new CreeperPowerEvent(entity, CreeperPowerEvent.PowerCause.SET_OFF);
            server.getPluginManager().callEvent(event);

            if (!event.isCancelled())
                ((CreeperBridge)getHandle()).setPowered(false);
        }
    }

    @Override
    public void setMaxFuseTicks(int ticks) {
        Preconditions.checkArgument(ticks >= 0, "ticks < 0");

        ((CreeperBridge)getHandle()).setFuseTimeBF(ticks);
    }

    @Override
    public int getMaxFuseTicks() {
        return ((CreeperBridge)getHandle()).getFuseTimeBF();
    }

    @Override
    public void setExplosionRadius(int radius) {
        Preconditions.checkArgument(radius >= 0, "radius < 0");

        ((CreeperBridge)getHandle()).setExplosionRadiusBF(radius);
    }

    @Override
    public int getExplosionRadius() {
        return ((CreeperBridge)getHandle()).getExplosionRadiusBF();
    }

    @Override
    public void explode() {
        ((CreeperBridge)getHandle()).explodeBF();
    }

    @Override
    public void ignite() {
        getHandle().ignite();
    }

    @Override
    public net.minecraft.world.entity.monster.Creeper getHandle() {
        return (net.minecraft.world.entity.monster.Creeper) entity;
    }

    @Override
    public String toString() {
        return "Creeper";
    }

    @Override
    public EntityType getType() {
        return EntityType.CREEPER;
    }

    @Override
    public int getFuseTicks() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isIgnited() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setIgnited(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getHeadRotationSpeed() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMaxHeadPitch() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void lookAt(@NotNull Location arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lookAt(@NotNull Entity arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lookAt(@NotNull Location arg0, float arg1, float arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lookAt(@NotNull Entity arg0, float arg1, float arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lookAt(double arg0, double arg1, double arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lookAt(double arg0, double arg1, double arg2, float arg3, float arg4) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setFuseTicks(int arg0) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void ignite(@NotNull Entity entity) {
		// TODO Auto-generated method stub
		getHandle().ignite();
	}

	@Override
	public @Nullable Entity getIgniter() {
        // TODO Mixin creeper
		// return this.getHandle().entityIgniter != null ? this.getHandle().entityIgniter.getBukkitEntity() : null;
		return null;
	}

}

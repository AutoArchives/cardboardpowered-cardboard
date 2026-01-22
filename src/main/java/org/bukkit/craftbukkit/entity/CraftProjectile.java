package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.cardboardpowered.impl.entity.AbstractProjectile;
import org.cardboardpowered.impl.entity.LivingEntityImpl;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

import org.cardboardpowered.interfaces.IMixinEntity;

public abstract class CraftProjectile extends AbstractProjectile implements Projectile {

    public CraftProjectile(CraftServer server, net.minecraft.world.entity.projectile.Projectile entity) {
        super(server, entity);
    }

    @Override
    public ProjectileSource getShooter() {
        return ((IMixinEntity)getHandle()).getProjectileSourceBukkit();
    }

    @Override
    public void setShooter(ProjectileSource shooter) {
        if (shooter instanceof LivingEntityImpl) getHandle().setOwner((LivingEntity) ((LivingEntityImpl) shooter).nms);
        else getHandle().setOwner(null);
        ((IMixinEntity)getHandle()).setProjectileSourceBukkit(shooter);
    }

    @Override
    public net.minecraft.world.entity.projectile.Projectile getHandle() {
        return (net.minecraft.world.entity.projectile.Projectile) nms;
    }

    @Override
    public String toString() {
        return "CraftProjectile";
    }

}
package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

import org.cardboardpowered.bridge.world.entity.EntityBridge;

public abstract class CraftProjectile extends AbstractProjectile implements Projectile {

    public CraftProjectile(CraftServer server, net.minecraft.world.entity.projectile.Projectile entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.projectile.Projectile getHandle() {
        return (net.minecraft.world.entity.projectile.Projectile) entity;
    }

    @Override
    public String toString() {
        return "CraftProjectile";
    }

}
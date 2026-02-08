package org.bukkit.craftbukkit.entity;

import net.kyori.adventure.text.Component;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;
import org.jetbrains.annotations.Nullable;

public class CraftSnowball extends CraftThrowableProjectile implements Snowball {

    public CraftSnowball(CraftServer server, net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball getHandle() {
        return (net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball) entity;
    }

    @Override
    public String toString() {
        return "CraftSnowball";
    }

    @Override
    public EntityType getType() {
        return EntityType.SNOWBALL;
    }

    @Override
    public @Nullable Component customName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void customName(@Nullable Component arg0) {
        // TODO Auto-generated method stub
        
    }

}
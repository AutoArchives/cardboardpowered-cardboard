package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownExpBottle;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownExperienceBottle;

public class CardboardThrownExpBottle extends ThrowableProjectileImpl implements ThrownExpBottle {

    public CardboardThrownExpBottle(CraftServer server, Projectile entity) {
        super(server, entity);
    }

    @Override
    public ThrownExperienceBottle getHandle() {
        return (ThrownExperienceBottle) nms;
    }

    @Override
    public String toString() {
        return "EntityThrownExpBottle";
    }

    @Override
    public EntityType getType() {
        return EntityType.EXPERIENCE_BOTTLE;
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
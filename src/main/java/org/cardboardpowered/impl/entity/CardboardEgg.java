package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import net.minecraft.entity.projectile.thrown.EggEntity;

public class CardboardEgg extends ThrowableProjectileImpl implements Egg {

    public CardboardEgg(CraftServer server, EggEntity entity) {
        super(server, entity);
    }

    @Override
    public EggEntity getHandle() {
        return (EggEntity) nms;
    }

    @Override
    public String toString() {
        return "CardboardEgg";
    }

    @Override
    public EntityType getType() {
        return EntityType.EGG;
    }

}
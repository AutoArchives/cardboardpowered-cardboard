package org.cardboardpowered.impl.entity;

import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;

public class CardboardEgg extends ThrowableProjectileImpl implements Egg {

    public CardboardEgg(CraftServer server, ThrownEgg entity) {
        super(server, entity);
    }

    @Override
    public ThrownEgg getHandle() {
        return (ThrownEgg) entity;
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
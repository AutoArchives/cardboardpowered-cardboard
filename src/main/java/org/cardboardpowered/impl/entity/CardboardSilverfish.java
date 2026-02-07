package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftMonster;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;

public class CardboardSilverfish extends CraftMonster implements Silverfish {

    public CardboardSilverfish(CraftServer server, net.minecraft.world.entity.monster.Silverfish entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.Silverfish getHandle() {
        return (net.minecraft.world.entity.monster.Silverfish) entity;
    }

    @Override
    public String toString() {
        return "CardboardSilverfish";
    }

    @Override
    public EntityType getType() {
        return EntityType.SILVERFISH;
    }

}
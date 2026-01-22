package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;

public class CardboardGiant extends CraftMonster implements Giant {

    public CardboardGiant(CraftServer server, net.minecraft.world.entity.monster.Giant entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.Giant getHandle() {
        return (net.minecraft.world.entity.monster.Giant) nms;
    }

    @Override
    public String toString() {
        return "Giant";
    }

    @Override
    public EntityType getType() {
        return EntityType.GIANT;
    }

}
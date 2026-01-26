package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;

public class CardboardBlaze extends CraftMonster implements Blaze {

    public CardboardBlaze(CraftServer server, net.minecraft.world.entity.monster.Blaze entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.Blaze getHandle() {
        return (net.minecraft.world.entity.monster.Blaze) entity;
    }

    @Override
    public String toString() {
        return "Blaze";
    }

    @Override
    public EntityType getType() {
        return EntityType.BLAZE;
    }

}
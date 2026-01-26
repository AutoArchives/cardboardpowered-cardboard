package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MagmaCube;

public class CardboardMagmaCube extends CraftSlime implements MagmaCube {

    public CardboardMagmaCube(CraftServer server, net.minecraft.world.entity.monster.MagmaCube entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.MagmaCube getHandle() {
        return (net.minecraft.world.entity.monster.MagmaCube) entity;
    }

    @Override
    public String toString() {
        return "MagmaCubeImpl";
    }

    @Override
    public EntityType getType() {
        return EntityType.MAGMA_CUBE;
    }

}
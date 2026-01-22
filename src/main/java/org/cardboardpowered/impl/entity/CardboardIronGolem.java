package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;

public class CardboardIronGolem extends CardboardGolem implements IronGolem {

    public CardboardIronGolem(CraftServer server, net.minecraft.world.entity.animal.golem.IronGolem entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.golem.IronGolem getHandle() {
        return (net.minecraft.world.entity.animal.golem.IronGolem) nms;
    }

    @Override
    public String toString() {
        return "IronGolem";
    }

    @Override
    public boolean isPlayerCreated() {
        return getHandle().isPlayerCreated();
    }

    @Override
    public void setPlayerCreated(boolean playerCreated) {
        getHandle().setPlayerCreated(playerCreated);
    }

    @Override
    public EntityType getType() {
        return EntityType.IRON_GOLEM;
    }

}
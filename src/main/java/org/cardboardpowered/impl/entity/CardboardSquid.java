package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Squid;

public class CardboardSquid extends CraftAgeable implements Squid {

    public CardboardSquid(CraftServer server, net.minecraft.entity.passive.SquidEntity entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.passive.SquidEntity getHandle() {
        return (net.minecraft.entity.passive.SquidEntity) this.nms;
    }

    @Override
    public String toString() {
        return "CraftSquid";
    }

}
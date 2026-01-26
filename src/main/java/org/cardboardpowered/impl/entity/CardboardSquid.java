package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Squid;

public class CardboardSquid extends CraftAgeable implements Squid {

    public CardboardSquid(CraftServer server, net.minecraft.world.entity.animal.squid.Squid entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.squid.Squid getHandle() {
        return (net.minecraft.world.entity.animal.squid.Squid) this.entity;
    }

    @Override
    public String toString() {
        return "CraftSquid";
    }

}
package org.cardboardpowered.impl.entity;

import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.RideableMinecart;

public class CardboardMinecartRideable extends CardboardMinecart implements RideableMinecart {

    public CardboardMinecartRideable(CraftServer server, AbstractMinecart entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "MinecartRideable";
    }

    @Override
    public EntityType getType() {
        return EntityType.MINECART;
    }

}
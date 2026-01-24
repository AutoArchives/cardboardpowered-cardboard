package org.cardboardpowered.impl.entity;

import net.minecraft.world.entity.animal.golem.AbstractGolem;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Golem;

public class CardboardGolem extends CraftCreature implements Golem {

    public CardboardGolem(CraftServer server, AbstractGolem entity) {
        super(server, entity);
    }

    @Override
    public AbstractGolem getHandle() {
        return (AbstractGolem) nms;
    }

    @Override
    public String toString() {
        return "Golem";
    }

}
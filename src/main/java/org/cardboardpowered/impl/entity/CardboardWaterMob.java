package org.cardboardpowered.impl.entity;

import net.minecraft.world.entity.animal.fish.WaterAnimal;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.WaterMob;

public class CardboardWaterMob extends CraftCreature implements WaterMob {

    public CardboardWaterMob(CraftServer server, WaterAnimal entity) {
        super(server, entity);
    }

    @Override
    public WaterAnimal getHandle() {
        return (WaterAnimal) nms;
    }

    @Override
    public String toString() {
        return "WaterMob";
    }

}
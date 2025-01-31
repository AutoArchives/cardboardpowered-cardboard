package org.cardboardpowered.impl.entity;

import net.minecraft.entity.mob.CaveSpiderEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftSpider;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;

public class CardboardCaveSpider extends CraftSpider implements CaveSpider {

    public CardboardCaveSpider(CraftServer server, CaveSpiderEntity entity) {
        super(server, entity);
    }

    @Override
    public CaveSpiderEntity getHandle() {
        return (CaveSpiderEntity) nms;
    }

    @Override
    public String toString() {
        return "Cavespider";
    }

    @Override
    public EntityType getType() {
        return EntityType.CAVE_SPIDER;
    }

}
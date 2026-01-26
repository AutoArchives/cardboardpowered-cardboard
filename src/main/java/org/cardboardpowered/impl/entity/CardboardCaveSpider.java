package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftSpider;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;

public class CardboardCaveSpider extends CraftSpider implements CaveSpider {

    public CardboardCaveSpider(CraftServer server, net.minecraft.world.entity.monster.spider.CaveSpider entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.spider.CaveSpider getHandle() {
        return (net.minecraft.world.entity.monster.spider.CaveSpider) entity;
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
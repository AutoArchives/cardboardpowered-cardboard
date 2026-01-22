package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spider;
import org.cardboardpowered.impl.entity.CraftMonster;

public class CraftSpider extends CraftMonster implements Spider {

    public CraftSpider(CraftServer server, net.minecraft.world.entity.monster.spider.Spider entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.spider.Spider getHandle() {
        return (net.minecraft.world.entity.monster.spider.Spider) nms;
    }

    @Override
    public String toString() {
        return "CraftSpider";
    }

    @Override
    public EntityType getType() {
        return EntityType.SPIDER;
    }

}
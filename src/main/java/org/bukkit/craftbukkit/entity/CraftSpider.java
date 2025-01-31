package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.mob.SpiderEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spider;
import org.cardboardpowered.impl.entity.MonsterImpl;

public class CraftSpider extends MonsterImpl implements Spider {

    public CraftSpider(CraftServer server, SpiderEntity entity) {
        super(server, entity);
    }

    @Override
    public SpiderEntity getHandle() {
        return (SpiderEntity) nms;
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
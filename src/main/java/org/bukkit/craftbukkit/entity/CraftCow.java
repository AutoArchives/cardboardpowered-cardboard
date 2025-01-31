package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.CowEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.cardboardpowered.impl.entity.AnimalsImpl;

public class CraftCow extends AnimalsImpl implements Cow {

    public CraftCow(CraftServer server, CowEntity entity) {
        super(server, entity);
    }

    @Override
    public CowEntity getHandle() {
        return (CowEntity) nms;
    }

    @Override
    public String toString() {
        return "CraftCow";
    }

    @Override
    public EntityType getType() {
        return EntityType.COW;
    }

}
package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import org.cardboardpowered.interfaces.ISlimeEntity;

public class CraftSlime extends CraftMob implements Slime {

    public CraftSlime(CraftServer server, net.minecraft.world.entity.monster.Slime entity) {
        super(server, entity);
    }

    @Override
    public int getSize() {
        return getHandle().getSize();
    }

    @Override
    public void setSize(int size) {
        ((ISlimeEntity)getHandle()).setSizeBF(size, true);
    }

    @Override
    public net.minecraft.world.entity.monster.Slime getHandle() {
        return (net.minecraft.world.entity.monster.Slime) nms;
    }

    @Override
    public String toString() {
        return "CraftSlime";
    }

    @Override
    public EntityType getType() {
        return EntityType.SLIME;
    }

    @Override
    public boolean canWander() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setWander(boolean arg0) {
    }

}
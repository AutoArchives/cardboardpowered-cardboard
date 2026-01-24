package org.cardboardpowered.impl.entity;

import net.minecraft.world.entity.PathfinderMob;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class CraftCreature extends CraftMob implements Creature {

    public CraftCreature(CraftServer server, PathfinderMob entity) {
        super(server, entity);
    }

    @Override
    public PathfinderMob getHandle() {
        return (PathfinderMob) nms;
    }

    @Override
    public String toString() {
        return "CraftCreature";
    }

    @Override
    public int getHeadRotationSpeed() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMaxHeadPitch() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void lookAt(@NotNull Location arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lookAt(@NotNull Entity arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lookAt(@NotNull Location arg0, float arg1, float arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lookAt(@NotNull Entity arg0, float arg1, float arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lookAt(double arg0, double arg1, double arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lookAt(double arg0, double arg1, double arg2, float arg3, float arg4) {
        // TODO Auto-generated method stub
        
    }

}
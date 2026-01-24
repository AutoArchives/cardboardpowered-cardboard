package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftSkeleton;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Stray;

public class StrayImpl extends CraftSkeleton implements Stray {

    public StrayImpl(CraftServer server, net.minecraft.world.entity.monster.skeleton.Stray entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "Stray";
    }

    @Override
    public EntityType getType() {
        return EntityType.STRAY;
    }

    @SuppressWarnings("deprecation")
    @Override
    public SkeletonType getSkeletonType() {
        return SkeletonType.STRAY;
    }

}
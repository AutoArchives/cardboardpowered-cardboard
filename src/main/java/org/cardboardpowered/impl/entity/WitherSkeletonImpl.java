package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftSkeleton;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WitherSkeleton;

public class WitherSkeletonImpl extends CraftSkeleton implements WitherSkeleton {

    public WitherSkeletonImpl(CraftServer server, net.minecraft.world.entity.monster.skeleton.WitherSkeleton entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "WitherSkeletonImpl";
    }

    @Override
    public EntityType getType() {
        return EntityType.WITHER_SKELETON;
    }

    @SuppressWarnings("deprecation")
    @Override
    public SkeletonType getSkeletonType() {
        return SkeletonType.WITHER;
    }

}
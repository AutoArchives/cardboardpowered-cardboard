package org.cardboardpowered.impl.entity;

import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.ComplexLivingEntity;

public abstract class CardboardComplexEntity extends CraftLivingEntity implements ComplexLivingEntity {

    public CardboardComplexEntity(CraftServer server, LivingEntity entity) {
        super(server, entity);
    }

    @Override
    public LivingEntity getHandle() {
        return (LivingEntity) entity;
    }

}
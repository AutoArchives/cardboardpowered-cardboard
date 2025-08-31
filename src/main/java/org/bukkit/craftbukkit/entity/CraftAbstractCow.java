package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.AbstractCowEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.cardboardpowered.impl.entity.CraftAnimals;
//import org.bukkit.entity.AbstractCow;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class CraftAbstractCow
extends CraftAnimals

// TODO: Update API
// implements AbstractCow

{

    public CraftAbstractCow(CraftServer server, AbstractCowEntity entity) {
        super(server, entity);
    }

    @Override
    public AbstractCowEntity getHandle() {
        return (AbstractCowEntity)this.nms;
    }

}


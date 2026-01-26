package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.animal.cow.AbstractCow;
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

    public CraftAbstractCow(CraftServer server, AbstractCow entity) {
        super(server, entity);
    }

    @Override
    public AbstractCow getHandle() {
        return (AbstractCow)this.entity;
    }

}


package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.cardboardpowered.impl.entity.CraftAnimals;

public class CraftOcelot extends CraftAnimals implements Ocelot {
 
    public CraftOcelot(CraftServer server, net.minecraft.world.entity.animal.feline.Ocelot ocelot) {
        super(server, ocelot);
    }

    @Override
    public net.minecraft.world.entity.animal.feline.Ocelot getHandle() {
        return (net.minecraft.world.entity.animal.feline.Ocelot) nms;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Type getCatType() {
        return Type.WILD_OCELOT;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setCatType(Type type) {
        throw new UnsupportedOperationException("Cats are now a different entity!");
    }

    @Override
    public String toString() {
        return "Ocelot";
    }

    @Override
    public EntityType getType() {
        return EntityType.OCELOT;
    }

    @Override
    public boolean isTrusting() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setTrusting(boolean bl) {
        // TODO Auto-generated method stub
    }

}
package org.cardboardpowered.impl.entity;

import java.util.UUID;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.ChestedHorse;
import org.jetbrains.annotations.Nullable;

public abstract class CardboardChestedHorse extends CardboardAbstractHorse implements ChestedHorse {

    public CardboardChestedHorse(CraftServer server, AbstractChestedHorse entity) {
        super(server, entity);
    }

    @Override
    public AbstractChestedHorse getHandle() {
        return (AbstractChestedHorse)super.getHandle();
    }

    @Override
    public boolean isCarryingChest() {
        return this.getHandle().hasChest();
    }

    @Override
    public void setCarryingChest(boolean chest) {
        if (chest == isCarryingChest()) return;

        this.getHandle().setChest(chest);
        // this.getHandle().onChestedStatusChanged();
    }

    @Override
    public boolean isEating() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEatingGrass() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isRearing() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setEating(boolean bl) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setEatingGrass(boolean bl) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setRearing(boolean bl) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public @Nullable UUID getOwnerUniqueId() {
        // TODO Auto-generated method stub
        return null;
    }

}
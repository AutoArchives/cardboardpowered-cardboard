package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftIllager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Vindicator;

public class CardboardVindicator extends CraftIllager implements Vindicator {

    public CardboardVindicator(CraftServer server, net.minecraft.world.entity.monster.illager.Vindicator entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.illager.Vindicator getHandle() {
        return (net.minecraft.world.entity.monster.illager.Vindicator) super.getHandle();
    }

    @Override
    public String toString() {
        return "Vindicator";
    }

    @Override
    public EntityType getType() {
        return EntityType.VINDICATOR;
    }

    @Override
    public boolean isJohnny() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setJohnny(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

}
package org.cardboardpowered.impl.entity;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.Drowned;

public class CardboardDrowned extends CraftZombie implements Drowned {

    public CardboardDrowned(CraftServer server, net.minecraft.world.entity.monster.zombie.Drowned entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.zombie.Drowned getHandle() {
        return (net.minecraft.world.entity.monster.zombie.Drowned) entity;
    }

    @Override
    public String toString() {
        return "Drowned";
    }

    @Override
    public EntityType getType() {
        return EntityType.DROWNED;
    }

    @Override
    public void rangedAttack(LivingEntity arg0, float arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setChargingAttack(boolean arg0) {
        // TODO Auto-generated method stub
    }

}
package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftIllager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pillager;
import org.bukkit.inventory.Inventory;

public class CardboardPillager extends CraftIllager implements Pillager {

    public CardboardPillager(CraftServer server, net.minecraft.world.entity.monster.illager.Pillager entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.illager.Pillager getHandle() {
        return (net.minecraft.world.entity.monster.illager.Pillager) super.getHandle();
    }

    @Override
    public EntityType getType() {
        return EntityType.PILLAGER;
    }

    @Override
    public String toString() {
        return "Pillager";
    }

    @Override
    public Inventory getInventory() {
        return null; // TODO  inventory is not visible.
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
package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.EntityType;

public class CardboardGuardianElder extends CardboardGuardian implements ElderGuardian {

    public CardboardGuardianElder(CraftServer server, net.minecraft.world.entity.monster.ElderGuardian entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "ElderGuardian";
    }

    @Override
    public EntityType getType() {
        return EntityType.ELDER_GUARDIAN;
    }

    @Override
    public boolean isElder() {
        return true;
    }

}
package org.cardboardpowered.impl.entity;

import net.minecraft.world.entity.ambient.AmbientCreature;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.EntityType;

public class CardboardAmbient extends CraftMob implements Ambient {

    public CardboardAmbient(CraftServer server, AmbientCreature entity) {
        super(server, entity);
    }

    @Override
    public AmbientCreature getHandle() {
        return (AmbientCreature) nms;
    }

    @Override
    public String toString() {
        return "Ambient";
    }

    @Override
    public EntityType getType() {
        return EntityType.UNKNOWN;
    }

}
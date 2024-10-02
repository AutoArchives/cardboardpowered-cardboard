package org.cardboardpowered.impl.entity;

import net.kyori.adventure.text.Component;
import net.minecraft.entity.ExperienceOrbEntity;

import java.util.UUID;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.jetbrains.annotations.Nullable;

public class ExperienceOrbImpl extends CraftEntity implements ExperienceOrb {

    public ExperienceOrbImpl(CraftServer server, ExperienceOrbEntity entity) {
        super(entity);
    }

    @Override
    public int getExperience() {
        return -1;// TODO return getHandle().amount;
    }

    @Override
    public void setExperience(int value) {
        // TODO getHandle().amount = value;
    }

    @Override
    public ExperienceOrbEntity getHandle() {
        return (ExperienceOrbEntity) nms;
    }

    @Override
    public String toString() {
        return "ExperienceOrbImpl";
    }

    @Override
    public EntityType getType() {
        return EntityType.EXPERIENCE_ORB;
    }

    @Override
    public UUID getSourceEntityId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SpawnReason getSpawnReason() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UUID getTriggerEntityId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public @Nullable Component customName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void customName(@Nullable Component arg0) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return getHandle().getExperienceAmount();
	}

	@Override
	public void setCount(int count) {
		// TODO Auto-generated method stub

	}

}
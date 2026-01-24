package org.cardboardpowered.impl.entity;

import net.kyori.adventure.sound.Sound.Source;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowman;
import org.jetbrains.annotations.NotNull;

public class CardboardSnowman extends CardboardGolem implements Snowman {

    public CardboardSnowman(CraftServer server, SnowGolem entity) {
        super(server, entity);
    }

    @Override
    public boolean isDerp() {
        return !getHandle().hasPumpkin();
    }

    @Override
    public void setDerp(boolean derpMode) {
        getHandle().setPumpkin(!derpMode);
    }

    @Override
    public SnowGolem getHandle() {
        return (SnowGolem) nms;
    }

    @Override
    public String toString() {
        return "Snowman";
    }

    @Override
    public EntityType getType() {
        return EntityType.SNOW_GOLEM;
    }

    @Override
    public void rangedAttack(LivingEntity arg0, float arg1) {
    }

    @Override
    public void setChargingAttack(boolean arg0) {
    }
    
    // 1.19.4:

	@Override
	public boolean readyToBeSheared() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void shear(@NotNull Source arg0) {
		// TODO Auto-generated method stub
		
	}

}
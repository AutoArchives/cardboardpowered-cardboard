package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;

public class CardboardGuardian extends CraftMonster implements Guardian {

    public CardboardGuardian(CraftServer server, net.minecraft.world.entity.monster.Guardian entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "Guardian";
    }

    @Override
    public EntityType getType() {
        return EntityType.GUARDIAN;
    }

    @Override
    public boolean isElder() {
        return false;
    }

    @Override
    public void setElder(boolean shouldBeElder) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean hasLaser() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean setLaser(boolean bl) {
        // TODO Auto-generated method stub
        return false;
    }
    
    // 1.19.4:

    @Override
    public net.minecraft.world.entity.monster.Guardian getHandle() {
        return (net.minecraft.world.entity.monster.Guardian)super.getHandle();
    }
    
	@Override
	public int getLaserDuration() {
        return this.getHandle().getAttackDuration();
	}

	@Override
	public int getLaserTicks() {
        //GuardianEntity.FireBeamGoal goal = this.getHandle().guardianAttackGoal;
        //return goal != null ? goal.beamTicks : -10;
		return 0;
	}

	@Override
	public boolean isMoving() {
        return this.getHandle().isMoving();
	}

	@Override
	public void setLaserTicks(int arg0) {
		// TODO Auto-generated method stub
		
	}

}
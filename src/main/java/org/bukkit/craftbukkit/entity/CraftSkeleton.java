package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.cardboardpowered.impl.entity.CraftMonster;

@SuppressWarnings("deprecation")
public class CraftSkeleton extends CraftMonster implements Skeleton {

    public CraftSkeleton(CraftServer server, AbstractSkeleton entity) {
        super(server, entity);
    }

    @Override
    public AbstractSkeleton getHandle() {
        return (AbstractSkeleton) nms;
    }

    @Override
    public String toString() {
        return "SkeletonImpl";
    }

    @Override
    public EntityType getType() {
        return EntityType.SKELETON;
    }

    @Override
    public SkeletonType getSkeletonType() {
       return SkeletonType.NORMAL;
    }

    @Override
    public void setSkeletonType(SkeletonType type) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void rangedAttack(LivingEntity arg0, float arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setChargingAttack(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setShouldBurnInDay(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean shouldBurnInDay() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getConversionTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isConverting() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setConversionTime(int arg0) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public int inPowderedSnowTime() {
		// TODO Auto-generated method stub
		return 0;
	}

}
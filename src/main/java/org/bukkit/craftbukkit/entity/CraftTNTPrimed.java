package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.cardboardpowered.impl.entity.LivingEntityImpl;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;

import org.cardboardpowered.bridge.world.entity.EntityBridge;
import org.cardboardpowered.bridge.world.entity.item.PrimedTntBridge;

public class CraftTNTPrimed extends CraftEntity implements TNTPrimed {

    public CraftTNTPrimed(CraftServer server, PrimedTnt entity) {
        super(entity);
    }

    @Override
    public float getYield() {
        return 0f; // TODO return getHandle().yield;
    }

    @Override
    public boolean isIncendiary() {
        return false; // TODO return getHandle().isIncendiary;
    }

    @Override
    public void setIsIncendiary(boolean isIncendiary) {
        // TODO getHandle().isIncendiary = isIncendiary;
    }

    @Override
    public void setYield(float yield) {
        // TODO getHandle().yield = yield;
    }

    @Override
    public int getFuseTicks() {
        return getHandle().getFuse();
    }

    @Override
    public void setFuseTicks(int fuseTicks) {
        getHandle().setFuse(fuseTicks);
    }

    @Override
    public PrimedTnt getHandle() {
        return (PrimedTnt) entity;
    }

    @Override
    public String toString() {
        return "TNT";
    }

    @Override
    public EntityType getType() {
        return EntityType.TNT;
    }

    @Override
    public Entity getSource() {
        LivingEntity source = getHandle().getOwner(); //.getCausingEntity();
        return (source != null) ? ((EntityBridge)source).getBukkitEntity() : null;
    }

    public void setSource(Entity source) {
        if (source instanceof LivingEntity) {
            ((PrimedTntBridge)getHandle()).cardboard$setSource(((LivingEntityImpl) source).getHandle());
        } else ((PrimedTntBridge)getHandle()).cardboard$setSource(null);
    }
    
    // 1.20.4 API:

	@Override
	public void setBlockData(@NotNull BlockData data) {
		this.getHandle().setBlockState(((CraftBlockData)data).getState());
	}

	@Override
	public @NotNull BlockData getBlockData() {
        return CraftBlockData.fromData(this.getHandle().getBlockState());
	}

}

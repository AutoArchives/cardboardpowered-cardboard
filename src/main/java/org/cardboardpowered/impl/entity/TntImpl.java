package org.cardboardpowered.impl.entity;

import net.kyori.adventure.text.Component;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;

import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.cardboardpowered.impl.entity.LivingEntityImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;

import com.javazilla.bukkitfabric.interfaces.IMixinEntity;
import org.cardboardpowered.interfaces.ITnt;

public class TntImpl extends CraftEntity implements TNTPrimed {

    public TntImpl(CraftServer server, TntEntity entity) {
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
    public TntEntity getHandle() {
        return (TntEntity) nms;
    }

    @Override
    public String toString() {
        return "TNT";
    }

    @Override
    public EntityType getType() {
        return EntityType.PRIMED_TNT;
    }

    @Override
    public Entity getSource() {
        LivingEntity source = getHandle().getOwner(); //.getCausingEntity();
        return (source != null) ? ((IMixinEntity)source).getBukkitEntity() : null;
    }

    public void setSource(Entity source) {
        if (source instanceof LivingEntity) {
            ((ITnt)getHandle()).setSourceBF(((LivingEntityImpl) source).getHandle());
        } else ((ITnt)getHandle()).setSourceBF(null);
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

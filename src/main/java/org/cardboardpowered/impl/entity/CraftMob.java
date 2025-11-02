package org.cardboardpowered.impl.entity;

import net.kyori.adventure.util.TriState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Random;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Projectile;
import org.bukkit.loot.LootTable;
import org.bukkit.util.Vector;

import com.destroystokyo.paper.entity.Pathfinder;
import com.google.common.base.Preconditions;

import org.cardboardpowered.CardboardMod;
import org.cardboardpowered.interfaces.IMixinEntity;

import me.isaiah.common.cmixin.IMixinMobEntity;

public class CraftMob extends LivingEntityImpl implements Mob {

    protected final Random random = new Random();

    public CraftMob(CraftServer server, MobEntity entity) {
        super(server, entity);
    }

    @Override
    public void setTarget(LivingEntity target) {
        // TODO
    }

    @Override
    public LivingEntityImpl getTarget() {
        if (getHandle().getTarget() == null) return null;
        return (LivingEntityImpl) ((IMixinEntity)getHandle().getTarget()).getBukkitEntity();
    }

    @Override
    public void setAware(boolean aware) {
        // TODO
    }

    @Override
    public boolean isAware() {
        // TODO
        return false;
    }

    @Override
    public MobEntity getHandle() {
        return (MobEntity) nms;
    }

    @Override
    public String toString() {
        return "CraftMob";
    }

    @Override
    public void setLootTable(LootTable table) {
    	IMixinMobEntity ic = (IMixinMobEntity) (Object) getHandle();
    	if (table == null) {
    		ic.IC$set_loot_table(null);
    		return;
    	}

    	ic.IC$set_loot_table( CraftNamespacedKey.toMinecraft(table.getKey()) );
        // getHandle().lootTable = (table == null) ? null : CraftNamespacedKey.toMinecraft(table.getKey());
    }

    @Override
    public LootTable getLootTable() {
        //if (getHandle().lootTable == null)
        //    getHandle().lootTable = getHandle().getLootTable();
        //NamespacedKey key = CraftNamespacedKey.fromMinecraft(getHandle().lootTable);
        IMixinMobEntity ic = (IMixinMobEntity) (Object) getHandle();
        NamespacedKey key = CraftNamespacedKey.fromMinecraft(ic.IC$get_loot_table_id());
        return Bukkit.getLootTable(key);
    }

    @Override
    public void setSeed(long seed) {
        getHandle().lootTableSeed = seed;
    }

    @Override
    public long getSeed() {
        return getHandle().lootTableSeed;
    }

    // Paper start
    public boolean isInDaylight() {
        if (getHandle().getEntityWorld().isDay()) {
            float f = getHandle().getBrightnessAtEyes();
            BlockPos blockPos = getHandle().getVehicle() instanceof BoatEntity ? BlockPos.ofFloored(getHandle().getX(), Math.round(getHandle().getY()), getHandle().getZ()).up() : BlockPos.ofFloored(getHandle().getX(), Math.round(getHandle().getY()), getHandle().getZ());
            if (f > 0.5f && CardboardMod.random.nextFloat() * 30.0f < (f - 0.4f) * 2.0f && getHandle().getEntityWorld().isSkyVisible(blockPos)) return true;
        }
        return false;
    }

    @Override
    public Pathfinder getPathfinder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getHeadRotationSpeed() {
    	return this.getHandle().getMaxLookYawChange();
    }

    @Override
    public int getMaxHeadPitch() {
    	return this.getHandle().getMaxLookPitchChange();
    }

    @Override
    public void lookAt(@NotNull Location location) {
    	Preconditions.checkNotNull(location, "location cannot be null");
    	Preconditions.checkArgument(location.getWorld().equals(this.getWorld()), "location in a different world");
    	this.getHandle().getLookControl().lookAt(location.getX(), location.getY(), location.getZ());
    }

    @Override
    public void lookAt(@NotNull Location location, float headRotationSpeed, float maxHeadPitch) {
    	Preconditions.checkNotNull(location, "location cannot be null");
    	Preconditions.checkArgument(location.getWorld().equals(this.getWorld()), "location in a different world");
    	this.getHandle().getLookControl().lookAt(location.getX(), location.getY(), location.getZ(), headRotationSpeed, maxHeadPitch);
    }

    @Override
    public void lookAt(@NotNull org.bukkit.entity.Entity entity) {
    	Preconditions.checkNotNull(entity, "entity cannot be null");
    	Preconditions.checkArgument(entity.getWorld().equals(this.getWorld()), "entity in a different world");
    	this.getHandle().getLookControl().lookAt(((CraftEntity)entity).getHandle());
    }

    @Override
    public void lookAt(@NotNull org.bukkit.entity.Entity entity, float headRotationSpeed, float maxHeadPitch) {
    	Preconditions.checkNotNull(entity, "entity cannot be null");
    	Preconditions.checkArgument(entity.getWorld().equals(this.getWorld()), "entity in a different world");
    	this.getHandle().getLookControl().lookAt(((CraftEntity)entity).getHandle(), headRotationSpeed, maxHeadPitch);
    }

    @Override
    public void lookAt(double x, double y, double z) {
    	this.getHandle().getLookControl().lookAt(x, y, z);
    }

    @Override
    public void lookAt(double x, double y, double z, float headRotationSpeed, float maxHeadPitch) {
    	this.getHandle().getLookControl().lookAt(x, y, z, headRotationSpeed, maxHeadPitch);
    }

    @Override
    public boolean isLeftHanded() {
    	return this.getHandle().isLeftHanded();
    }

    @Override
    public void setLeftHanded(boolean bl) {
    	this.getHandle().setLeftHanded(bl);
    }

	@Override
	public Sound getAmbientSound() {
		return Sound.AMBIENT_CAVE;
	}

	@Override
	public int getPossibleExperienceReward() {
        return this.getHandle().getExperienceToDrop((ServerWorld) this.getHandle().getEntityWorld()); // getXpToDrop();
	}
	// 1.20.2 API:
	
	public boolean isAggressive() {
        return this.getHandle().isAttacking();
    }

    public void setAggressive(boolean aggressive) {
        this.getHandle().setAttacking(aggressive);
    }

	@Override
	public boolean shouldDespawnInPeaceful() {
		
		return !this.getHandle().getType().isAllowedInPeaceful();
		
		// return this.getHandle().isDisallowedInPeaceful();
		// return this.getHandle().shouldActuallyDespawnInPeaceful();
	}

	@Override
	public void setDespawnInPeacefulOverride(TriState state) {
		// TODO
		// this.getHandle().despawnInPeacefulOverride = state;
	}

	@Override
	public TriState getDespawnInPeacefulOverride() {
		return TriState.NOT_SET;
		// TODO return this.getHandle().despawnInPeacefulOverride;
	}
	


}

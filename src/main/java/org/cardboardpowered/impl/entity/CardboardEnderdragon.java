package org.cardboardpowered.impl.entity;

import com.destroystokyo.paper.entity.Pathfinder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import org.cardboardpowered.interfaces.IMixinEntity;

import java.util.Set;

import net.kyori.adventure.util.TriState;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.loot.LootTable;
import org.cardboardpowered.impl.CardboardDragonBattle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CardboardEnderdragon extends CardboardComplexEntity implements EnderDragon {

    public CardboardEnderdragon(CraftServer server, net.minecraft.world.entity.boss.enderdragon.EnderDragon entity) {
        super(server, entity);
    }

    @Override
    public Set<ComplexEntityPart> getParts() {
        Builder<ComplexEntityPart> builder = ImmutableSet.builder();
        for (EnderDragonPart part : getHandle().subEntities)
            builder.add((ComplexEntityPart) ((IMixinEntity)part).getBukkitEntity());
        return builder.build();
    }

    @Override
    public net.minecraft.world.entity.boss.enderdragon.EnderDragon getHandle() {
        return (net.minecraft.world.entity.boss.enderdragon.EnderDragon) nms;
    }

    @Override
    public String toString() {
        return "Dragon";
    }

    @Override
    public EntityType getType() {
        return EntityType.ENDER_DRAGON;
    }

    @Override
    public Phase getPhase() {
        return Phase.values()[getHandle().getEntityData().get(net.minecraft.world.entity.boss.enderdragon.EnderDragon.DATA_PHASE)];
    }

    @Override
    public void setPhase(Phase phase) {
        getHandle().getPhaseManager().setPhase(getMinecraftPhase(phase));
    }

    public static Phase getBukkitPhase(EnderDragonPhase phase) {
        return Phase.values()[phase.getId()];
    }

    public static EnderDragonPhase getMinecraftPhase(Phase phase) {
        return EnderDragonPhase.getById(phase.ordinal());
    }

    @Override
    public BossBar getBossBar() {
        return getDragonBattle().getBossBar();
    }

    @Override
    public DragonBattle getDragonBattle() {
        return new CardboardDragonBattle(getHandle().getDragonFight());
    }

    @Override
    public int getDeathAnimationTicks() {
        return getHandle().dragonDeathTime;
    }

    @Override
    public Pathfinder getPathfinder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LivingEntity getTarget() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAware() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInDaylight() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setAware(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTarget(LivingEntity arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LootTable getLootTable() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getSeed() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setLootTable(LootTable arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setSeed(long arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getHeadRotationSpeed() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMaxHeadPitch() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void lookAt(@NotNull Location arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lookAt(@NotNull Entity arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lookAt(@NotNull Location arg0, float arg1, float arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lookAt(@NotNull Entity arg0, float arg1, float arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lookAt(double arg0, double arg1, double arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lookAt(double arg0, double arg1, double arg2, float arg3, float arg4) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isLeftHanded() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setLeftHanded(boolean bl) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public @NotNull Location getPodium() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPodium(@Nullable Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public @Nullable Sound getAmbientSound() {
		return Sound.ENTITY_ENDER_DRAGON_AMBIENT;
	}
	
    public int getPossibleExperienceReward() {
        return this.getHandle().getExperienceReward((net.minecraft.server.level.ServerLevel) this.getHandle().level(), null);
    }

    // 1.20.2 API
    
	@Override
	public boolean isAggressive() {
        return this.getHandle().isAggressive();
	}

	@Override
	public void setAggressive(boolean aggressive) {
        this.getHandle().setAggressive(aggressive);
	}
	
	// TODO Check extend CraftMob:
	
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
	
	/*
	public final boolean shouldDespawnInPeaceful() {
	      return this.despawnInPeacefulOverride.toBooleanOrElse(!this.getType().isTypeAllowedInPeaceful());
	   }
	*/
}

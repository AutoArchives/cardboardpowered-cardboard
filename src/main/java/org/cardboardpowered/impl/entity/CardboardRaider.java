package org.cardboardpowered.impl.entity;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Raider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CardboardRaider extends CraftMonster implements Raider {

    public CardboardRaider(CraftServer server, net.minecraft.world.entity.raid.Raider entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.raid.Raider getHandle() {
        return (net.minecraft.world.entity.raid.Raider) super.getHandle();
    }

    @Override
    public String toString() {
        return "Raider";
    }

    @Override
    public Block getPatrolTarget() {
        return getHandle().getPatrolTarget() == null ? null : CraftBlock.at((ServerLevel) getHandle().level(), getHandle().getPatrolTarget());
    }

    @Override
    public void setPatrolTarget(Block block) {
        if (block == null) {
            getHandle().setPatrolTarget((BlockPos) null);
        } else {
            Preconditions.checkArgument(block.getWorld().equals(this.getWorld()), "Block must be in same world");
            getHandle().setPatrolTarget(new BlockPos(block.getX(), block.getY(), block.getZ()));
        }
    }

    @Override
    public boolean isPatrolLeader() {
        return getHandle().isPatrolLeader();
    }

    @Override
    public void setPatrolLeader(boolean leader) {
        getHandle().setPatrolLeader(leader);
    }

    @Override
    public boolean isCanJoinRaid() {
        return getHandle().canJoinRaid();
    }

    @Override
    public void setCanJoinRaid(boolean join) {
        getHandle().setCanJoinRaid(join);
    }

	@Override
	public @NotNull Sound getCelebrationSound() {
        return CraftSound.getBukkit(this.getHandle().getCelebrateSound());

	}

	@Override
	public boolean isCelebrating() {
        return this.getHandle().isCelebrating();
	}

	@Override
	public void setCelebrating(boolean arg0) {
        this.getHandle().setCelebrating(arg0);
	}

	@Override
	public void setRaid(@Nullable Raid raid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public @Nullable Raid getRaid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getWave() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setWave(int wave) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTicksOutsideRaid() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTicksOutsideRaid(int ticks) {
		// TODO Auto-generated method stub
		
	}

}
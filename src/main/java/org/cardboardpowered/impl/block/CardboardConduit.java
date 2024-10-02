package org.cardboardpowered.impl.block;

import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Conduit;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CardboardConduit extends CardboardBlockEntityState<ConduitBlockEntity> implements Conduit {

	/*
    public CardboardConduit(Block block) {
        super(block, ConduitBlockEntity.class);
    }

    public CardboardConduit(Material material, ConduitBlockEntity te) {
        super(material, te);
    }
    */
    
    public CardboardConduit(World world, ConduitBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardConduit(CardboardConduit state, Location location) {
        super(state, location);
    }

    @Override
    public CardboardConduit copy() {
        return new CardboardConduit(this, null);
    }

    @Override
    public CardboardConduit copy(Location location) {
        return new CardboardConduit(this, location);
    }

    public boolean isActive() {
        // this.ensureNoWorldGeneration();
        ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
        return conduit != null && conduit.isActive();
    }

    public boolean isHunting() {
        // this.ensureNoWorldGeneration();
        ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
        return conduit != null && conduit.isEyeOpen();
    }

	@Override
	public int getRange() {
        // this.ensureNoWorldGeneration();
        // ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
        //return conduit != null ? ConduitBlockEntity.getRange(conduit.activatingBlocks) : 0;
		return 0;
	}

	@Override
	public org.bukkit.entity.@Nullable LivingEntity getTarget() {
		ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
        if (conduit == null) {
            return null;
        }
        
        // TODO
        return null;
        // LivingEntity nmsEntity = conduit.targetEntity;
        //return nmsEntity != null ? (org.bukkit.entity.LivingEntity)nmsEntity.getBukkitEntity() : null;
	}
	
	// 1.20.6 API

	@Override
	public @NotNull Collection<Block> getFrameBlocks() {
       
		/*
		this.ensureNoWorldGeneration();
        ArrayList<Block> blocks = new ArrayList<Block>();
        ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
        if (conduit != null) {
            for (BlockPos position : conduit.activatingBlocks) {
                blocks.add(CraftBlock.at((ServerWorld)this.getWorldHandle(), position));
            }
        }
        return blocks;*/
		
		return null;
	}

	@Override
	public int getFrameBlockCount() {
		return 0;
		//this.ensureNoWorldGeneration();
        //ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
        //return conduit != null ? conduit.activatingBlocks.size() : 0;
	}

	@Override
	public boolean setTarget(org.bukkit.entity.@Nullable LivingEntity target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasTarget() {
        return false;
		//ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
        //return conduit != null && conduit.targetEntity != null && conduit.targetEntity.isAlive();
    }

	@Override
	public @NotNull BoundingBox getHuntingArea() {
		return null;
		//Box bounds = ConduitBlockEntity.getAttackZone(this.getPosition());
        //return new BoundingBox(bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ);
	}
	
    private void ensureNoWorldGeneration() {
		// TODO Auto-generated method stub
		
	}

    /*public Collection<Block> getFrameBlocks() {
        this.ensureNoWorldGeneration();
        ArrayList<Block> blocks = new ArrayList<Block>();
        ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
        if (conduit != null) {
            for (BlockPos position : conduit.activatingBlocks) {
                blocks.add(CraftBlock.at(this.getWorldHandle(), position));
            }
        }
        return blocks;
    }

    private void ensureNoWorldGeneration() {
		// TODO Auto-generated method stub
		
	}

	public int getFrameBlockCount() {
        this.ensureNoWorldGeneration();
        ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
        return conduit != null ? conduit.activatingBlocks.size() : 0;
    }

    public int getRange() {
        this.ensureNoWorldGeneration();
        ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
        return conduit != null ? ConduitBlockEntity.getRange(conduit.activatingBlocks) : 0;
    }

    public boolean setTarget(org.bukkit.entity.LivingEntity target) {
        ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
        if (conduit == null) {
            return false;
        }
        LivingEntity currentTarget = conduit.targetEntity;
        if (target == null) {
            if (currentTarget == null) {
                return false;
            }
            conduit.targetEntity = null;
            conduit.targetUuid = null;
        } else {
            if (currentTarget != null && target.getUniqueId().equals(currentTarget.getUuid())) {
                return false;
            }
            conduit.targetEntity = ((CraftLivingEntity)target).getHandle();
            conduit.targetUuid = target.getUniqueId();
        }
        ConduitBlockEntity.updateDestroyTarget(conduit.getWorld(), this.getPosition(), this.data, conduit.activatingBlocks, conduit, false);
        return true;
    }

    public org.bukkit.entity.LivingEntity getTarget() {
        ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
        if (conduit == null) {
            return null;
        }
        LivingEntity nmsEntity = conduit.targetEntity;
        return nmsEntity != null ? (org.bukkit.entity.LivingEntity)nmsEntity.getBukkitEntity() : null;
    }

    public boolean hasTarget() {
        ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
        return conduit != null && conduit.targetEntity != null && conduit.targetEntity.isAlive();
    }

    public BoundingBox getHuntingArea() {
        Box bounds = ConduitBlockEntity.getAttackZone(this.getPosition());
        return new BoundingBox(bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ);
    }*/

}
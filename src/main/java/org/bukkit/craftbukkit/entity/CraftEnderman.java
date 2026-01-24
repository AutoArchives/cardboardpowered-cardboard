package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;
import org.cardboardpowered.impl.entity.CraftMonster;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class CraftEnderman extends CraftMonster implements Enderman {

    public CraftEnderman(CraftServer server, EnderMan entity) {
        super(server, entity);
    }

    @Override
    public MaterialData getCarriedMaterial() {
        BlockState blockData = getHandle().getCarriedBlock();
        return (blockData == null) ? Material.AIR.getNewData((byte) 0) : CraftMagicNumbers.getMaterial(blockData);
    }

    @Override
    public BlockData getCarriedBlock() {
        BlockState blockData = getHandle().getCarriedBlock();
        return (blockData == null) ? null : CraftBlockData.fromData(blockData);
    }

    @Override
    public void setCarriedMaterial(MaterialData data) {
        getHandle().setCarriedBlock(CraftMagicNumbers.getBlock(data));
    }

    @Override
    public void setCarriedBlock(BlockData blockData) {
        getHandle().setCarriedBlock(blockData == null ? null : ((CraftBlockData) blockData).getState());
    }

    @Override
    public EnderMan getHandle() {
        return (EnderMan) nms;
    }

    @Override
    public String toString() {
        return "FabricEnderman";
    }

    @Override
    public EntityType getType() {
        return EntityType.ENDERMAN;
    }

    @Override
    public boolean teleportRandomly() {
        // TODO Auto-generated method stub
        return false;
    }

	@Override
	public boolean hasBeenStaredAt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isScreaming() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setHasBeenStaredAt(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setScreaming(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean teleport() {
		// return this.getHandle().teleportRandomly();
		return false;
	}

	@Override
	public boolean teleportTowards(@NotNull Entity entity) {
		// return this.getHandle().teleportTo(((CraftEntity)entity).getHandle());;
		return false;
	}
}

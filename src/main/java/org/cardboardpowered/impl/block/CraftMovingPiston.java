package org.cardboardpowered.impl.block;


import io.papermc.paper.block.MovingPiston;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public class CraftMovingPiston extends CardboardBlockEntityState<PistonMovingBlockEntity> implements MovingPiston {

    public CraftMovingPiston(World world, PistonMovingBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CraftMovingPiston(CraftMovingPiston state, Location location) {
        super(state, location);
    }

    @Override
    public CraftMovingPiston copy() {
        return new CraftMovingPiston(this, null);
    }

    @Override
    public CraftMovingPiston copy(Location location) {
        return new CraftMovingPiston(this, location);
    }

    public BlockData getMovingBlock() {
        return CraftBlockData.fromData(((PistonMovingBlockEntity)this.getTileEntity()).getMovedState());
    }

    public BlockFace getDirection() {
        return CraftBlock.notchToBlockFace(((PistonMovingBlockEntity)this.getTileEntity()).getDirection());
    }

    public boolean isExtending() {
        return ((PistonMovingBlockEntity)this.getTileEntity()).isExtending();
    }

    public boolean isPistonHead() {
        return ((PistonMovingBlockEntity)this.getTileEntity()).isSourcePiston();
    }

}
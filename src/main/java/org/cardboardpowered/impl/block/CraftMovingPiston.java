package org.cardboardpowered.impl.block;


import io.papermc.paper.block.MovingPiston;
import net.minecraft.block.entity.PistonBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public class CraftMovingPiston extends CardboardBlockEntityState<PistonBlockEntity> implements MovingPiston {

    public CraftMovingPiston(World world, PistonBlockEntity tileEntity) {
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
        return CraftBlockData.fromData(((PistonBlockEntity)this.getTileEntity()).getPushedBlock());
    }

    public BlockFace getDirection() {
        return CraftBlock.notchToBlockFace(((PistonBlockEntity)this.getTileEntity()).getFacing());
    }

    public boolean isExtending() {
        return ((PistonBlockEntity)this.getTileEntity()).isExtending();
    }

    public boolean isPistonHead() {
        return ((PistonBlockEntity)this.getTileEntity()).isSource();
    }

}
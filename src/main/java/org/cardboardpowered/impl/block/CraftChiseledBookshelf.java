package org.cardboardpowered.impl.block;


import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.util.math.Vec2f;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ChiseledBookshelf;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.ChiseledBookshelfInventory;
import org.bukkit.util.Vector;

public class CraftChiseledBookshelf
extends CardboardBlockEntityState<ChiseledBookshelfBlockEntity>
implements ChiseledBookshelf {
    public CraftChiseledBookshelf(World world, ChiseledBookshelfBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CraftChiseledBookshelf(CraftChiseledBookshelf state, Location location) {
        super(state, location);
    }

    public int getLastInteractedSlot() {
        return ((ChiseledBookshelfBlockEntity)this.getSnapshot()).getLastInteractedSlot();
    }

    public void setLastInteractedSlot(int lastInteractedSlot) {
        // TODO
    	// ((ChiseledBookshelfBlockEntity)this.getSnapshot()).lastInteractedSlot = lastInteractedSlot;
    }

    public ChiseledBookshelfInventory getSnapshotInventory() {
        // return new CraftInventoryChiseledBookshelf((ChiseledBookshelfBlockEntity)this.getSnapshot());
    	return null;
    }

    public ChiseledBookshelfInventory getInventory() {
        if (!this.isPlaced()) {
            return this.getSnapshotInventory();
        }
        return null;
        // return new CraftInventoryChiseledBookshelf((ChiseledBookshelfBlockEntity)this.getTileEntity());
    }

    public int getSlot(Vector clickVector) {
        Vec2f faceVector;
        BlockFace facing = ((Directional)this.getBlockData()).getFacing();
        switch (facing) {
            case NORTH: {
                faceVector = new Vec2f((float)(1.0 - clickVector.getX()), (float)clickVector.getY());
                break;
            }
            case SOUTH: {
                faceVector = new Vec2f((float)clickVector.getX(), (float)clickVector.getY());
                break;
            }
            case WEST: {
                faceVector = new Vec2f((float)clickVector.getZ(), (float)clickVector.getY());
                break;
            }
            case EAST: {
                faceVector = new Vec2f((float)(1.0 - clickVector.getZ()), (float)clickVector.getY());
                break;
            }
            default: {
                return -1;
            }
        }
        return CraftChiseledBookshelf.getHitSlot(faceVector);
    }

    private static int getHitSlot(Vec2f vec2f) {
        // int i2 = vec2f.y >= 0.5f ? 0 : 1;
        // int j = ChiseledBookshelfBlock.getColumn(vec2f.x);
        // return j + i2 * 3;
    	return -1;
    }

    @Override
    public CraftChiseledBookshelf copy() {
        return new CraftChiseledBookshelf(this, null);
    }

    @Override
    public CraftChiseledBookshelf copy(Location location) {
        return new CraftChiseledBookshelf(this, location);
    }
}
package org.cardboardpowered.impl.block;

import org.cardboardpowered.impl.world.WorldImpl;

import com.google.common.base.Preconditions;

import me.isaiah.common.ICommonMod;
import me.isaiah.common.cmixin.IMixinBlockEntity;
import me.isaiah.common.cmixin.IMixinMinecraftServer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.persistence.PersistentDataContainer;

public abstract class CardboardBlockEntityState<T extends BlockEntity> extends CraftBlockState implements TileState {

    // private final Class<T> tileEntityClass;
    private final T tileEntity;
    private final T snapshot;
    
    public boolean snapshotDisabled;
    public static boolean DISABLE_SNAPSHOT = false;

    public CardboardBlockEntityState(World world, T tileEntity) {
        super(world, ((BlockEntity)tileEntity).getPos(), ((BlockEntity)tileEntity).getCachedState());
        this.tileEntity = tileEntity;
        try {
            this.snapshotDisabled = DISABLE_SNAPSHOT;
            this.snapshot = DISABLE_SNAPSHOT ? this.tileEntity : this.createSnapshot(tileEntity);
            if (this.snapshot != null) {
                this.load(this.snapshot);
            }
        }
        catch (Throwable thr) {
            if (thr instanceof ThreadDeath) {
                throw (ThreadDeath)thr;
            }
            throw new RuntimeException("Failed to read BlockState at: world: " + this.getWorld().getName() + " location: (" + this.getX() + ", " + this.getY() + ", " + this.getZ() + ")", thr);
        }
    }
    
    protected CardboardBlockEntityState(CardboardBlockEntityState<T> state, Location location) {
        super(state, location);
        this.tileEntity = this.createSnapshot(state.snapshot);
        this.snapshot = this.tileEntity;
        this.loadData(state.getSnapshotNBT());
    }
    
    public void loadData(NbtCompound nbtTagCompound) {
        ((BlockEntity)this.snapshot).read(nbtTagCompound, ICommonMod.getIServer().getMinecraft().getRegistryManager());
        this.load(this.snapshot);
    }
    
    /*
    @Deprecated
    private CardboardBlockEntityState(Block block, Class<T> tileEntityClass) {
        super(block);
        this.tileEntityClass = tileEntityClass;

        WorldImpl world = (WorldImpl) this.getWorld();
        this.tileEntity = tileEntityClass.cast(world.getHandle().getBlockEntity(this.getPosition()));
        Preconditions.checkState(this.tileEntity != null, "BlockEntity = null. async access? " + block);
        //this.snapshot = this.createSnapshot(tileEntity);
        //this.load(snapshot);
        
        this.snapshotDisabled = DISABLE_SNAPSHOT;
        this.snapshot = DISABLE_SNAPSHOT ? this.tileEntity : this.createSnapshot(tileEntity);
        if (this.snapshot != null) {
            this.load(this.snapshot);
        }
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    private CardboardBlockEntityState(Material material, T tileEntity) {
        super(material);
        this.tileEntityClass = (Class<T>) tileEntity.getClass();
        this.tileEntity = tileEntity;
        this.snapshot = this.createSnapshot(tileEntity);
        this.load(snapshot);
    }
    */

    @SuppressWarnings("unchecked")
    private T createSnapshot(T tileEntity) {
        if (tileEntity == null) return null;

        IMixinBlockEntity ic = (IMixinBlockEntity)tileEntity;
        NbtCompound nbtTagCompound = ic.I_createNbtWithIdentifyingData();

        IMixinMinecraftServer mc = ((IMixinMinecraftServer)ICommonMod.getIServer().getMinecraft());
        T snapshot = (T) mc.IC$create_blockentity_from_nbt(getPosition(), data, nbtTagCompound);
        // T snapshot = (T) BlockEntity.createFromNbt(getPosition(), data, nbtTagCompound);
        return snapshot;
    }
    
    public void applyComponents(ComponentMap datacomponentmap, ComponentChanges datacomponentpatch) {
        ((BlockEntity)this.snapshot).readComponents(datacomponentmap, datacomponentpatch);
        this.load(this.snapshot);
    }
    
    public NbtCompound getSnapshotCustomNbtOnly() {
        this.applyTo(this.snapshot);
        
        
        NbtCompound nbt = ((BlockEntity)this.snapshot).createComponentlessNbt(CraftServer.server.getRegistryManager());
        ((BlockEntity)this.snapshot).removeFromCopiedStackNbt(nbt);
        return nbt;
    }
    
    public ComponentMap collectComponents() {
        return ((BlockEntity)this.snapshot).createComponentMap();
    }

    private void copyData(T from, T to) {
        BlockPos pos = to.getPos();
        IMixinBlockEntity ic = (IMixinBlockEntity)tileEntity;
        NbtCompound nbtTagCompound = ic.I_createNbtWithIdentifyingData();
        to.setCachedState(data);
        
        IMixinBlockEntity ic2 = (IMixinBlockEntity)to;
        ic2.IC$read_nbt(nbtTagCompound);
        // to.readNbt(nbtTagCompound);
        to.pos = (pos);
    }

    public T getTileEntity() {
        return tileEntity;
    }

    public T getSnapshot() {
        return snapshot;
    }

    protected BlockEntity getTileEntityFromWorld() {
        requirePlaced();
        return ((WorldImpl) getWorld()).getHandle().getBlockEntity(getPosition());
    }

    public NbtCompound getSnapshotNBT() {
        applyTo(snapshot);
        IMixinBlockEntity ic = (IMixinBlockEntity)snapshot;
        return ic.I_createNbtWithIdentifyingData();
    }

    public void load(T blockEntity) {
        if (tileEntity != null && tileEntity != snapshot) copyData(blockEntity, snapshot);
    }

    /*public void applyTo(T blockEntity) {
        if (tileEntity != null && tileEntity != snapshot) copyData(snapshot, blockEntity);
    }*/
    
    protected void applyTo(T tileEntity) {
        if (tileEntity != null && tileEntity != this.snapshot) {
            this.copyData(this.snapshot, tileEntity);
        }
    }

    public boolean isApplicable(BlockEntity tileEntity) {
        return tileEntity != null && this.tileEntity.getClass() == tileEntity.getClass();
    	//return tileEntityClass.isInstance(tileEntity);
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        BlockEntity tile;
        boolean result = super.update(force, applyPhysics);
        if (result && this.isPlaced() && this.isApplicable(tile = this.getTileEntityFromWorld())) {
            this.applyTo((T) tile);
            tile.markDirty();
        }
        return result;
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return ((com.javazilla.bukkitfabric.interfaces.IMixinBlockEntity)(Object)getSnapshot()).getPersistentDataContainer();
    }
    
    // @Override
    public abstract CardboardBlockEntityState<T> copy();

    // @Override
    public abstract CardboardBlockEntityState<T> copy(Location var1);

	@Override
	public boolean isSnapshot() {
		// TODO Auto-generated method stub
		return !this.snapshotDisabled;
	}

}
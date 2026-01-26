package org.cardboardpowered.impl.block;

import org.cardboardpowered.bridge.world.level.block.entity.BlockEntityBridge;
import org.cardboardpowered.impl.world.CraftWorld;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import me.isaiah.common.ICommonMod;
import me.isaiah.common.cmixin.IMixinBlockEntity;
import me.isaiah.common.cmixin.IMixinMinecraftServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.TileState;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.persistence.PersistentDataContainer;

/**
 * Class: CardboardBlockEntityState
 */
public abstract class CardboardBlockEntityState<T extends BlockEntity> extends CraftBlockState implements TileState {

	private static final Logger LOGGER = LogUtils.getLogger();
	
    // private final Class<T> tileEntityClass;
    private final T tileEntity;
    private final T snapshot;
    
    public boolean snapshotDisabled;
    public static boolean DISABLE_SNAPSHOT = false;

    public CardboardBlockEntityState(World world, T tileEntity) {
        super(world, ((BlockEntity)tileEntity).getBlockPos(), ((BlockEntity)tileEntity).getBlockState());
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

    public void loadData(CompoundTag tag) {
        try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(() -> "CraftBlockEntityState@" + this.getPosition().toShortString(), LOGGER);){
            ((BlockEntity)this.snapshot).loadWithComponents(TagValueInput.create(problemReporter, ICommonMod.getIServer().getMinecraft().registryAccess(), tag));
        }
        this.load(this.snapshot);
    }
    
    public RegistryAccess getRegistryAccess() {
        LevelAccessor worldHandle = this.getWorldHandle();
        return worldHandle != null ? worldHandle.registryAccess() : CraftRegistry.getMinecraftRegistry();
    }
    
    /*
    @Deprecated
    private CardboardBlockEntityState(Block block, Class<T> tileEntityClass) {
        super(block);
        this.tileEntityClass = tileEntityClass;

        CraftWorld world = (CraftWorld) this.getWorld();
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
        CompoundTag nbtTagCompound = ic.I_createNbtWithIdentifyingData();

        IMixinMinecraftServer mc = ((IMixinMinecraftServer)ICommonMod.getIServer().getMinecraft());
        T snapshot = (T) mc.IC$create_blockentity_from_nbt(getPosition(), data, nbtTagCompound);
        // T snapshot = (T) BlockEntity.createFromNbt(getPosition(), data, nbtTagCompound);
        return snapshot;
    }
    
    public void applyComponents(DataComponentMap datacomponentmap, DataComponentPatch datacomponentpatch) {
        ((BlockEntity)this.snapshot).applyComponents(datacomponentmap, datacomponentpatch);
        this.load(this.snapshot);
    }
    
    /*
    public NbtCompound getSnapshotCustomNbtOnly() {
        this.applyTo(this.snapshot);
        
        
        NbtCompound nbt = ((BlockEntity)this.snapshot).createComponentlessNbt(CraftServer.server.getRegistryManager());
        ((BlockEntity)this.snapshot).removeFromCopiedStackNbt(nbt);
        return nbt;
    }
    */

    public CompoundTag getSnapshotCustomNbtOnly() {
        this.applyTo(this.snapshot);
        try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(() -> "CraftBlockEntityState@" + this.getPosition().toShortString(), LOGGER);){


        	TagValueOutput output = /*NbtWriteView.*/createWrappingWithContext(problemReporter, this.getRegistryAccess(), ((BlockEntity)this.snapshot).saveCustomOnly(this.getRegistryAccess()));
            ((BlockEntity)this.snapshot).removeComponentsFromTag(output);
            if (!output.isEmpty()) {
                ((BlockEntity)this.snapshot).saveId(output);
            }
            CompoundTag nbtCompound = output.buildResult();
            return nbtCompound;
        }
    }

    // ErrorReporter.Logging, DynamicRegistryManager, NbtCompound
    // Paper start - utility methods
	public static TagValueOutput createWrappingWithContext(
			final ProblemReporter.ScopedCollector problemReporter,
			final RegistryAccess lookup,
			final CompoundTag compoundTag
		) {
            return new TagValueOutput(problemReporter, lookup.createSerializationContext(NbtOps.INSTANCE), compoundTag);
        }
	// Paper end - utility methods

    /*
    public NbtCompound getSnapshotCustomNbtOnly() {
        this.applyTo(this.snapshot);
        try (ErrorReporter.Logging problemReporter = new ErrorReporter.Logging(() -> "CraftBlockEntityState@" + this.getPosition().toShortString(), LOGGER);){
            NbtWriteView output = NbtWriteView.createWrappingWithContext(problemReporter, this.getRegistryAccess(), ((BlockEntity)this.snapshot).createComponentlessNbt(this.getRegistryAccess()));
            ((BlockEntity)this.snapshot).removeFromCopiedStackData(output);
            if (!output.isEmpty()) {
                ((BlockEntity)this.snapshot).writeId(output);
            }
            NbtCompound nbtCompound = output.getNbt();
            return nbtCompound;
        }
    }
    */

    public DataComponentMap collectComponents() {
        return ((BlockEntity)this.snapshot).collectComponents();
    }

    private void copyData(T from, T to) {
        BlockPos pos = to.getBlockPos();
        IMixinBlockEntity ic = (IMixinBlockEntity)tileEntity;
        CompoundTag nbtTagCompound = ic.I_createNbtWithIdentifyingData();
        to.setBlockState(data);
        
        IMixinBlockEntity ic2 = (IMixinBlockEntity)to;
        ic2.IC$read_nbt(nbtTagCompound);
        // to.readNbt(nbtTagCompound);
        to.worldPosition = (pos);
    }

    public T getTileEntity() {
        return tileEntity;
    }

    public T getSnapshot() {
        return snapshot;
    }

    protected BlockEntity getTileEntityFromWorld() {
        requirePlaced();
        return ((CraftWorld) getWorld()).getHandle().getBlockEntity(getPosition());
    }

    public CompoundTag getSnapshotNBT() {
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
            tile.setChanged();
        }
        return result;
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return ((BlockEntityBridge)(Object)getSnapshot()).getPersistentDataContainer();
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
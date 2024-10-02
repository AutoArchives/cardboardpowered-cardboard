package org.bukkit.craftbukkit.block;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.cardboardpowered.impl.world.CardboardChunk;

import com.google.common.base.Preconditions;
import org.cardboardpowered.impl.world.WorldImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import com.javazilla.bukkitfabric.interfaces.IMixinWorld;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

@SuppressWarnings("deprecation")
public class CraftBlockState implements BlockState {

    protected final WorldImpl world;
    
    @Deprecated
    private CardboardChunk chunk;
    
    
    private final BlockPos position;
    protected net.minecraft.block.BlockState data;
    protected int flag;
    
    private WeakReference<WorldAccess> weakWorld;

    public CraftBlockState(final Block block) {
        this.world = (WorldImpl) block.getWorld();
        this.position = ((CraftBlock) block).getPosition();
        this.data = ((CraftBlock) block).getNMS();
        this.chunk = (CardboardChunk) block.getChunk();
        this.flag = 3;
    }

    public CraftBlockState(final Block block, int flag) {
        this(block);
        this.flag = flag;
    }

    /*
    @Deprecated
    public CraftBlockState(Material material) {
        world = null;
        data = CraftMagicNumbers.getBlock(material).getDefaultState();
        position = BlockPos.ORIGIN;
    }
    */

    protected CraftBlockState(@Nullable World world, BlockPos blockPosition, net.minecraft.block.BlockState blockData) {
        this.world = (WorldImpl) world;
        this.position = blockPosition;
        this.data = blockData;
    }

    protected CraftBlockState(CraftBlockState state, @Nullable Location location) {
        if (location == null) {
            this.world = null;
            this.position = state.getPosition().toImmutable();
        } else {
            this.world = (WorldImpl)location.getWorld();
            this.position = CraftLocation.toBlockPosition(location);
        }
        this.data = state.data;
        this.flag = state.flag;
        this.setWorldHandle(state.getWorldHandle());
    }
    
    public void setWorldHandle(WorldAccess generatorAccess) {
        this.weakWorld = generatorAccess instanceof net.minecraft.world.World ? null : new WeakReference<WorldAccess>(generatorAccess);
    }

    public WorldAccess getWorldHandle() {
        if (this.weakWorld == null) {
            return this.isPlaced() ? this.world.getHandle() : null;
        }
        WorldAccess access = (WorldAccess)this.weakWorld.get();
        if (access == null) {
            this.weakWorld = null;
            return this.isPlaced() ? this.world.getHandle() : null;
        }
        return access;
    }
    
    protected final boolean isWorldGeneration() {
        WorldAccess generatorAccess = this.getWorldHandle();
        return generatorAccess != null && !(generatorAccess instanceof net.minecraft.world.World);
    }

	public static CraftBlockState getBlockState(WorldAccess world, net.minecraft.util.math.BlockPos pos) {
        return new CraftBlockState(CraftBlock.at((ServerWorld) world, pos));
    }

    public static CraftBlockState getBlockState(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos, int flag) {
        return new CraftBlockState(((IMixinWorld)(Object)world).getWorldImpl().getBlockAt(pos.getX(), pos.getY(), pos.getZ()), flag);
    }
    
    /*
    public CraftBlockState copy() {
        return new CraftBlockState(this, null);
    }

    public BlockState copy(Location location) {
        return new CraftBlockState(this, location);
    }
    */

    @Override
    public World getWorld() {
        requirePlaced();
        return world;
    }

    @Override
    public int getX() {
        return position.getX();
    }

    @Override
    public int getY() {
        return position.getY();
    }

    @Override
    public int getZ() {
        return position.getZ();
    }

    @Override
    public Chunk getChunk() {
        requirePlaced();
        
        return this.world.getChunkAt(this.getX() >> 4, this.getZ() >> 4);
        
        // return chunk;
    }

    public void setData(net.minecraft.block.BlockState data) {
        this.data = data;
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public net.minecraft.block.BlockState getHandle() {
        return this.data;
    }

    @Override
    public BlockData getBlockData() {
        return CraftBlockData.fromData(data);
    }

    @Override
    public void setBlockData(BlockData data) {
        Preconditions.checkArgument(data != null, "BlockData cannot be null");
        this.data = ((CraftBlockData) data).getState();
    }

    @Override
    public void setData(final MaterialData data) {
        Material mat = CraftMagicNumbers.getMaterial(this.data).getItemType();

        if ((mat == null) || (mat.getData() == null)) {
            this.data = CraftMagicNumbers.getBlock(data);
        } else {
            if ((data.getClass() == mat.getData()) || (data.getClass() == MaterialData.class)) {
                this.data = CraftMagicNumbers.getBlock(data);
            } else throw new IllegalArgumentException("Provided data is not of type " + mat.getData().getName() + ", found " + data.getClass().getName());
        }
    }

    @Override
    public MaterialData getData() {
        return CraftMagicNumbers.getMaterial(data);
    }

    @Override
    public void setType(final Material type) {
        Preconditions.checkArgument(type != null, "Material cannot be null");
        Preconditions.checkArgument(type.isBlock(), "Material must be a block!");

        if (this.getType() != type) this.data = CraftMagicNumbers.getBlock(type).getDefaultState();
    }

    @Override
    public Material getType() {
        return CraftMagicNumbers.getMaterial(data.getBlock());
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    @Override
    public byte getLightLevel() {
        return getBlock().getLightLevel();
    }

    @Override
    public CraftBlock getBlock() {
        requirePlaced();
        return CraftBlock.at((ServerWorld) world.getHandle(), position);
    }

    @Override
    public boolean update() {
        return update(false);
    }

    @Override
    public boolean update(boolean force) {
        return update(force, true);
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        if (!isPlaced()) return true;
        CraftBlock block = getBlock();

        if (block.getType() != getType() && !force) return false;

        net.minecraft.block.BlockState newBlock = this.data;
        block.setTypeAndData(newBlock, applyPhysics);
        world.getHandle().updateListeners(position, block.getNMS(), newBlock, 3);

        return true;
    }

    @Override
    public byte getRawData() {
        return CraftMagicNumbers.toLegacyData(data);
    }

    @Override
    public Location getLocation() {
        return new Location(world, getX(), getY(), getZ());
    }

    @Override
    public Location getLocation(Location loc) {
        if (loc != null) {
            loc.setWorld(world);
            loc.setX(getX());
            loc.setY(getY());
            loc.setZ(getZ());
            loc.setYaw(0);
            loc.setPitch(0);
        }
        return loc;
    }

    @Override
    public void setRawData(byte data) {
        this.data = CraftMagicNumbers.getBlock(getType(), data);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;

        final CraftBlockState other = (CraftBlockState) obj;
        if (world != other.world && (world == null || !this.world.equals(other.world))) return false;
        if (position != other.position && (position == null || !this.position.equals(other.position))) return false;
        if (data != other.data && (data == null || !this.data.equals(other.data))) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.world != null ? this.world.hashCode() : 0);
        hash = 73 * hash + (this.position != null ? this.position.hashCode() : 0);
        hash = 73 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        requirePlaced();
        this.world.getBlockMetadata().setMetadata(this.getBlock(), metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        requirePlaced();
        return this.world.getBlockMetadata().getMetadata(this.getBlock(), metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        requirePlaced();
        return this.world.getBlockMetadata().hasMetadata(this.getBlock(), metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        requirePlaced();
        this.world.getBlockMetadata().removeMetadata(this.getBlock(), metadataKey, owningPlugin);
    }

    @Override
    public boolean isPlaced() {
        return world != null;
    }

    protected void requirePlaced() {
        if (!isPlaced()) throw new IllegalStateException("The state must be placed to call this method");
    }

    @Override
    public boolean isCollidable() {
        // TODO Auto-generated method stub
        return false;
    }
    
    // 1.19.2 paper api:

	@Override
	public @Unmodifiable @NotNull Collection<ItemStack> getDrops() {
		// TODO Auto-generated method stub
		return getDrops(null, null);
	}

	@Override
	public @Unmodifiable @NotNull Collection<ItemStack> getDrops(@Nullable ItemStack arg0) {
		// TODO Auto-generated method stub
		return getDrops(arg0, null);
	}

	@Override
	public @Unmodifiable @NotNull Collection<ItemStack> getDrops(@NotNull ItemStack arg0, @Nullable Entity entity) {
        this.requirePlaced();
        net.minecraft.item.ItemStack nms = CraftItemStack.asNMSCopy(arg0);
        if (arg0 == null || !this.data.isToolRequired() || nms.isSuitableFor(this.data)) {
            return net.minecraft.block.Block.getDroppedStacks(this.data, this.world.getHandle(), this.position, this.world.getHandle().getBlockEntity(this.position), entity == null ? null : ((CraftEntity)entity).getHandle(), nms).stream().map(CraftItemStack::asBukkitCopy).toList();
        }
        return Collections.emptyList();
	}

	// 1.20.2 API:
	
	@Override
	public BlockState copy() {
		return new CraftBlockState(this, null);
	}

	@Override
	public BlockState copy(Location location) {
		return new CraftBlockState(this, location);
	}

}
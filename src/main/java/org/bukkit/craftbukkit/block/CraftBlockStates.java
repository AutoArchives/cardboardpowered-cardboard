package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.cardboardpowered.impl.world.WorldImpl;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.*;
import org.cardboardpowered.impl.block.*;

public final class CraftBlockStates {
    private static final Map<Material, BlockStateFactory<?>> FACTORIES = new HashMap();
    private static final BlockStateFactory<?> DEFAULT_FACTORY = new BlockStateFactory<CraftBlockState>(CraftBlockState.class){

        @Override
        public CraftBlockState createBlockState(World world, BlockPos blockPosition, net.minecraft.block.BlockState blockData, BlockEntity tileEntity) {
            Preconditions.checkState((tileEntity == null ? 1 : 0) != 0, (String)"Unexpected BlockState for %s", (Object)CraftBlockType.minecraftToBukkit(blockData.getBlock()));
            return new CraftBlockState(world, blockPosition, blockData);
        }
    };
    private static final Map<BlockEntityType<?>, BlockStateFactory<?>> FACTORIES_BY_BLOCK_ENTITY_TYPE = new HashMap();

    private static void register(BlockEntityType<?> type, BlockStateFactory<?> factory) {
        FACTORIES_BY_BLOCK_ENTITY_TYPE.put(type, factory);
    }

    private static void register(Material blockType, BlockStateFactory<?> factory) {
        FACTORIES.put(blockType, factory);
    }
    
    
    

    private static <T extends BlockEntity, B extends CardboardBlockEntityState<T>> void register(
    		BlockEntityType<? extends T> blockEntityType,
    		Class<B> blockStateType,
    		BiFunction<World, T, B> blockStateConstructor
    	) {
    	
    	BlockStateFactory<B> factory = new BlockEntityStateFactory<>(blockStateType, blockStateConstructor, blockEntityType); // Paper
    	
        // BlockEntityStateFactory<B> factory = new BlockEntityStateFactory<>(blockStateType, blockStateConstructor, blockEntityType::instantiate);
        for (net.minecraft.block.Block block : blockEntityType.blocks) {
            CraftBlockStates.register(CraftBlockType.minecraftToBukkit(block), factory);
        }
        CraftBlockStates.register(blockEntityType, factory);
    }

    /*
    private static <T extends BlockEntity, B extends CardboardBlockEntityState<T>> void register(BlockEntityType<? extends T> blockEntityType, Class<B> blockStateType, BiFunction<World, T, B> blockStateConstructor) {
        BlockEntityStateFactory<BlockEntity, B> factory =
        		new BlockEntityStateFactory<>(blockStateType, blockStateConstructor, blockEntityType::instantiate);

        for (net.minecraft.block.Block block : blockEntityType.blocks) {
            CraftBlockStates.register(CraftBlockType.minecraftToBukkit(block), factory);
        }
        CraftBlockStates.register(blockEntityType, factory);
    }*/

    private static BlockStateFactory<?> getFactory(Material material) {
        return FACTORIES.getOrDefault(material, DEFAULT_FACTORY);
    }

    private static BlockStateFactory<?> getFactory(Material material, BlockEntityType<?> type) {
        if (type != null) {
            return FACTORIES_BY_BLOCK_ENTITY_TYPE.getOrDefault(type, CraftBlockStates.getFactory(material));
        }
        return CraftBlockStates.getFactory(material);
    }

    public static Class<? extends CraftBlockState> getBlockStateType(Material material) {
        Preconditions.checkNotNull((Object)material, (Object)"material is null");
        return CraftBlockStates.getFactory((Material)material).blockStateType;
    }

    public static BlockEntity createNewTileEntity(Material material) {
        BlockStateFactory<?> factory = CraftBlockStates.getFactory(material);
        if (factory instanceof BlockEntityStateFactory) {
            return ((BlockEntityStateFactory)factory).createTileEntity(BlockPos.ORIGIN, CraftBlockType.bukkitToMinecraft(material).getDefaultState());
        }
        return null;
    }

    public static Class<? extends CraftBlockState> getBlockStateType(BlockEntityType<?> blockEntityType) {
        Preconditions.checkNotNull(blockEntityType, (Object)"blockEntityType is null");
        return CraftBlockStates.getFactory(null, blockEntityType).blockStateType;
    }

    public static BlockState getBlockState(Block block) {
        return CraftBlockStates.getBlockState(block, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static BlockState getBlockState(Block block, boolean useSnapshot) {
        Preconditions.checkNotNull((Object)block, (Object)"block is null");
        CraftBlock craftBlock = (CraftBlock)block;
        WorldImpl world = (WorldImpl)block.getWorld();
        BlockPos blockPosition = craftBlock.getPosition();
        net.minecraft.block.BlockState blockData = craftBlock.getNMS();
        BlockEntity tileEntity = craftBlock.getHandle().getBlockEntity(blockPosition);
        boolean prev = CardboardBlockEntityState.DISABLE_SNAPSHOT;
        CardboardBlockEntityState.DISABLE_SNAPSHOT = !useSnapshot;
        try {
            CraftBlockState blockState = CraftBlockStates.getBlockState(world, blockPosition, blockData, tileEntity);
            blockState.setWorldHandle(craftBlock.getHandle());
            CraftBlockState craftBlockState = blockState;
            return craftBlockState;
        }
        finally {
            CardboardBlockEntityState.DISABLE_SNAPSHOT = prev;
        }
    }

    @Deprecated
    public static BlockState getBlockState(BlockPos blockPosition, Material material, @Nullable NbtCompound blockEntityTag) {
        return CraftBlockStates.getBlockState(CraftServer.server.getRegistryManager(), blockPosition, material, blockEntityTag);
    }

    public static BlockState getBlockState(WorldView world, BlockPos blockPosition, Material material, @Nullable NbtCompound blockEntityTag) {
        return CraftBlockStates.getBlockState(world.getRegistryManager(), blockPosition, material, blockEntityTag);
    }

    public static BlockState getBlockState(DynamicRegistryManager registry, BlockPos blockPosition, Material material, @Nullable NbtCompound blockEntityTag) {
        Preconditions.checkNotNull((Object)material, (Object)"material is null");
        net.minecraft.block.BlockState blockData = CraftBlockType.bukkitToMinecraft(material).getDefaultState();
        return CraftBlockStates.getBlockState(registry, blockPosition, blockData, blockEntityTag);
    }

    @Deprecated
    public static BlockState getBlockState(net.minecraft.block.BlockState blockData, @Nullable NbtCompound blockEntityTag) {
        return CraftBlockStates.getBlockState(CraftServer.server.getRegistryManager(), BlockPos.ORIGIN, blockData, blockEntityTag);
    }

    public static BlockState getBlockState(WorldView world, BlockPos blockPosition, net.minecraft.block.BlockState blockData, @Nullable NbtCompound blockEntityTag) {
        return CraftBlockStates.getBlockState(world.getRegistryManager(), blockPosition, blockData, blockEntityTag);
    }

    public static BlockState getBlockState(DynamicRegistryManager registry, BlockPos blockPosition, net.minecraft.block.BlockState blockData, @Nullable NbtCompound blockEntityTag) {
        Preconditions.checkNotNull((Object)blockPosition, (Object)"blockPosition is null");
        Preconditions.checkNotNull((Object)blockData, (Object)"blockData is null");
        BlockEntity tileEntity = blockEntityTag == null ? null : BlockEntity.createFromNbt(blockPosition, blockData, blockEntityTag, registry);
        return CraftBlockStates.getBlockState(null, blockPosition, blockData, tileEntity);
    }

    public static CraftBlockState getBlockState(World world, BlockPos blockPosition, net.minecraft.block.BlockState blockData, BlockEntity tileEntity) {
        Material material = CraftBlockType.minecraftToBukkit(blockData.getBlock());
        BlockStateFactory<?> factory = world != null && tileEntity == null && CraftBlockStates.isTileEntityOptional(material) ? DEFAULT_FACTORY : CraftBlockStates.getFactory(material, tileEntity != null ? tileEntity.getType() : null);
        return factory.createBlockState(world, blockPosition, blockData, tileEntity);
    }

    public static boolean isTileEntityOptional(Material material) {
        return material == Material.MOVING_PISTON;
    }

    public static CraftBlockState getBlockState(WorldAccess world, BlockPos pos) {
        return new CraftBlockState(CraftBlock.at((ServerWorld)world, pos));
    }

    public static CraftBlockState getBlockState(WorldAccess world, BlockPos pos, int flag) {
        return new CraftBlockState(CraftBlock.at((ServerWorld)world, pos), flag);
    }

    private CraftBlockStates() {
    	//CardboardSign.class
    }

    static {
        CraftBlockStates.register(BlockEntityType.SIGN, CardboardSign.class, CardboardSign::new);
        CraftBlockStates.register(BlockEntityType.HANGING_SIGN, CraftHangingSign.class, CraftHangingSign::new);
        CraftBlockStates.register(BlockEntityType.SKULL, CardboardSkull.class, CardboardSkull::new);
        CraftBlockStates.register(BlockEntityType.COMMAND_BLOCK, CardboardCommandBlock.class, CardboardCommandBlock::new);
        CraftBlockStates.register(BlockEntityType.BANNER, CardboardBanner.class, CardboardBanner::new);
        CraftBlockStates.register(BlockEntityType.SHULKER_BOX, CardboardShulkerBox.class, CardboardShulkerBox::new);
        CraftBlockStates.register(BlockEntityType.BED, CardboardBed.class, CardboardBed::new);
        CraftBlockStates.register(BlockEntityType.BEEHIVE, CardboardBeehive.class, CardboardBeehive::new);
        CraftBlockStates.register(BlockEntityType.CAMPFIRE, CardboardCampfire.class, CardboardCampfire::new);
        CraftBlockStates.register(BlockEntityType.BARREL, CardboardBarrel.class, CardboardBarrel::new);
        CraftBlockStates.register(BlockEntityType.BEACON, CardboardBeacon.class, CardboardBeacon::new);
        CraftBlockStates.register(BlockEntityType.BELL, CardboardBell.class, CardboardBell::new);
        CraftBlockStates.register(BlockEntityType.BLAST_FURNACE, CardboardBlastFurnace.class, CardboardBlastFurnace::new);
        CraftBlockStates.register(BlockEntityType.BREWING_STAND, CardboardBrewingStand.class, CardboardBrewingStand::new);
        CraftBlockStates.register(BlockEntityType.CHEST, CardboardChest.class, CardboardChest::new);
        CraftBlockStates.register(BlockEntityType.CHISELED_BOOKSHELF, CraftChiseledBookshelf.class, CraftChiseledBookshelf::new);
        CraftBlockStates.register(BlockEntityType.COMPARATOR, CardboardComparator.class, CardboardComparator::new);
        CraftBlockStates.register(BlockEntityType.CONDUIT, CardboardConduit.class, CardboardConduit::new);
        CraftBlockStates.register(BlockEntityType.DAYLIGHT_DETECTOR, CardboardDaylightDetector.class, CardboardDaylightDetector::new);
        CraftBlockStates.register(BlockEntityType.DECORATED_POT, CraftDecoratedPot.class, CraftDecoratedPot::new);
        CraftBlockStates.register(BlockEntityType.DISPENSER, CardboardDispenser.class, CardboardDispenser::new);
        CraftBlockStates.register(BlockEntityType.DROPPER, CardboardDropper.class, CardboardDropper::new);
        CraftBlockStates.register(BlockEntityType.ENCHANTING_TABLE, CardboardEnchantingTable.class, CardboardEnchantingTable::new);
        CraftBlockStates.register(BlockEntityType.ENDER_CHEST, CardboardEnderchest.class, CardboardEnderchest::new);
        CraftBlockStates.register(BlockEntityType.END_GATEWAY, CardboardEndGateway.class, CardboardEndGateway::new);
        CraftBlockStates.register(BlockEntityType.END_PORTAL, CraftEndPortal.class, CraftEndPortal::new);
        CraftBlockStates.register(BlockEntityType.FURNACE, CardboardFurnaceFurnace.class, CardboardFurnaceFurnace::new);
        CraftBlockStates.register(BlockEntityType.HOPPER, CardboardHopper.class, CardboardHopper::new);
        CraftBlockStates.register(BlockEntityType.JIGSAW, CardboardJigsaw.class, CardboardJigsaw::new);
        CraftBlockStates.register(BlockEntityType.JUKEBOX, CardboardJukebox.class, CardboardJukebox::new);
        CraftBlockStates.register(BlockEntityType.LECTERN, CardboardLectern.class, CardboardLectern::new);
        CraftBlockStates.register(BlockEntityType.PISTON, CraftMovingPiston.class, CraftMovingPiston::new);
        CraftBlockStates.register(BlockEntityType.SCULK_CATALYST, CraftSculkCatalyst.class, CraftSculkCatalyst::new);
        CraftBlockStates.register(BlockEntityType.SCULK_SENSOR, CraftSculkSensor.class, CraftSculkSensor::new);
        CraftBlockStates.register(BlockEntityType.SCULK_SHRIEKER, CraftSculkShrieker.class, CraftSculkShrieker::new);
        CraftBlockStates.register(BlockEntityType.CALIBRATED_SCULK_SENSOR, CraftCalibratedSculkSensor.class, CraftCalibratedSculkSensor::new);
        CraftBlockStates.register(BlockEntityType.SMOKER, CardboardSmoker.class, CardboardSmoker::new);
        CraftBlockStates.register(BlockEntityType.MOB_SPAWNER, CardboardMobspawner.class, CardboardMobspawner::new);
        CraftBlockStates.register(BlockEntityType.STRUCTURE_BLOCK, CardboardStructureBlock.class, CardboardStructureBlock::new);
        CraftBlockStates.register(BlockEntityType.BRUSHABLE_BLOCK, CraftBrushableBlock.class, CraftBrushableBlock::new);
        CraftBlockStates.register(BlockEntityType.TRAPPED_CHEST, CardboardChest.class, CardboardChest::new);
        CraftBlockStates.register(BlockEntityType.CRAFTER, CraftCrafter.class, CraftCrafter::new);
        CraftBlockStates.register(BlockEntityType.TRIAL_SPAWNER, CraftTrialSpawner.class, CraftTrialSpawner::new);
        CraftBlockStates.register(BlockEntityType.VAULT, CraftVault.class, CraftVault::new);
    }

    private static class BlockEntityStateFactory<T extends BlockEntity, B extends CardboardBlockEntityState<T>>
    extends BlockStateFactory<B> {
        private final BiFunction<World, T, B> blockStateConstructor;
        // private final BiFunction<BlockPos, net.minecraft.block.BlockState, T> tileEntityConstructor;
        
        private final BlockEntityType<? extends T> tileEntityConstructor; // Paper

        // protected BlockEntityStateFactory(Class<B> blockStateType, BiFunction<World, T, B> blockStateConstructor, BiFunction<BlockPos, net.minecraft.world.level.block.state.BlockState, T> tileEntityConstructor) {
        
        protected BlockEntityStateFactory(Class<B> blockStateType, BiFunction<World, T, B> blockStateConstructor, BlockEntityType<? extends T> tileEntityConstructor) {
        	// Paper
        	super(blockStateType);
            this.blockStateConstructor = blockStateConstructor;
            this.tileEntityConstructor = tileEntityConstructor;
        }
        
        /*
        @Deprecated
        protected BlockEntityStateFactory(Class<B> blockStateType, BiFunction<World, T, B> blockStateConstructor, BiFunction<BlockPos, net.minecraft.block.BlockState, T> tileEntityConstructor) {
            super(blockStateType);
            this.blockStateConstructor = blockStateConstructor;
            this.tileEntityConstructor = tileEntityConstructor;
        }
        */

        @Override
        public final B createBlockState(World world, BlockPos blockPosition, net.minecraft.block.BlockState blockData, BlockEntity tileEntity) {
            if (world != null) {
                Preconditions.checkState((tileEntity != null ? 1 : 0) != 0, (String)"Tile is null, asynchronous access? %s", (Object)CraftBlock.at(((WorldImpl)world).getHandle(), blockPosition));
            } else if (tileEntity == null) {
                tileEntity = this.createTileEntity(blockPosition, blockData);
            }
            return this.createBlockState(world, (T) tileEntity);
        }

        private T createTileEntity(BlockPos blockPosition, net.minecraft.block.BlockState blockData) {
        	
            return (T)((BlockEntity)this.tileEntityConstructor.instantiate(blockPosition, blockData));
        }

        private B createBlockState(World world, T tileEntity) {
            return (B)((CardboardBlockEntityState)this.blockStateConstructor.apply(world, tileEntity));
        }
    }

    private static abstract class BlockStateFactory<B extends CraftBlockState> {
        public final Class<B> blockStateType;

        public BlockStateFactory(Class<B> blockStateType) {
            this.blockStateType = blockStateType;
        }

        public abstract B createBlockState(World var1, BlockPos var2, net.minecraft.block.BlockState var3, BlockEntity var4);
    }
}


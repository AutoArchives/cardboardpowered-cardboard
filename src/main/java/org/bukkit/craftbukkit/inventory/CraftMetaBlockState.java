package org.bukkit.craftbukkit.inventory;

// import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.MergedComponentMap;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.math.BlockPos;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.inventory.CraftMetaItem;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.util.BlockVector;
import org.cardboardpowered.impl.block.CardboardBlockEntityState;
import org.cardboardpowered.interfaces.IComponentChanges;
import org.cardboardpowered.interfaces.IMixinBlockEntity;

@DelegateDeserialization(value=SerializableMeta.class)
public class CraftMetaBlockState
extends CraftMetaItem
implements BlockStateMeta {
    private static final Set<Material> SHULKER_BOX_MATERIALS = Sets.newHashSet(new Material[]{Material.SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.PINK_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX, Material.CYAN_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.RED_SHULKER_BOX, Material.BLACK_SHULKER_BOX});
    private static final Set<Material> BLOCK_STATE_MATERIALS = Sets.newHashSet(
    		new Material[]{Material.FURNACE, Material.CHEST, Material.TRAPPED_CHEST, Material.JUKEBOX, Material.DISPENSER, Material.DROPPER,
    				Material.ACACIA_HANGING_SIGN, Material.ACACIA_SIGN, Material.ACACIA_WALL_HANGING_SIGN, Material.ACACIA_WALL_SIGN,
    				Material.BAMBOO_HANGING_SIGN, Material.BAMBOO_SIGN, Material.BAMBOO_WALL_HANGING_SIGN, Material.BAMBOO_WALL_SIGN,
    				Material.BIRCH_HANGING_SIGN, Material.BIRCH_SIGN, Material.BIRCH_WALL_HANGING_SIGN, Material.BIRCH_WALL_SIGN,
    				Material.CHERRY_HANGING_SIGN, Material.CHERRY_SIGN, Material.CHERRY_WALL_HANGING_SIGN, Material.CHERRY_WALL_SIGN,
    				Material.CRIMSON_HANGING_SIGN, Material.CRIMSON_SIGN, Material.CRIMSON_WALL_HANGING_SIGN, Material.CRIMSON_WALL_SIGN,
    				Material.DARK_OAK_HANGING_SIGN, Material.DARK_OAK_SIGN, Material.DARK_OAK_WALL_HANGING_SIGN, Material.DARK_OAK_WALL_SIGN,
    				Material.JUNGLE_HANGING_SIGN, Material.JUNGLE_SIGN, Material.JUNGLE_WALL_HANGING_SIGN, Material.JUNGLE_WALL_SIGN,
    				Material.MANGROVE_HANGING_SIGN, Material.MANGROVE_SIGN, Material.MANGROVE_WALL_HANGING_SIGN, Material.MANGROVE_WALL_SIGN,
    				Material.OAK_HANGING_SIGN, Material.OAK_SIGN, Material.OAK_WALL_HANGING_SIGN, Material.OAK_WALL_SIGN, Material.SPRUCE_HANGING_SIGN,
    				Material.SPRUCE_SIGN, Material.SPRUCE_WALL_HANGING_SIGN, Material.SPRUCE_WALL_SIGN, Material.WARPED_HANGING_SIGN,
    				Material.WARPED_SIGN, Material.WARPED_WALL_HANGING_SIGN, Material.WARPED_WALL_SIGN, Material.SPAWNER, Material.BREWING_STAND,
    				Material.ENCHANTING_TABLE, Material.COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.BEACON,
    				Material.DAYLIGHT_DETECTOR, Material.HOPPER, Material.COMPARATOR, Material.SHIELD, Material.STRUCTURE_BLOCK, Material.ENDER_CHEST,
    				Material.BARREL, Material.BELL, Material.BLAST_FURNACE, Material.CAMPFIRE, Material.SOUL_CAMPFIRE, Material.JIGSAW, Material.LECTERN,
    				Material.SMOKER, Material.BEEHIVE, Material.BEE_NEST, Material.SCULK_CATALYST, Material.SCULK_SHRIEKER,
    				// Material.CALIBRATED_SCULK_SENSOR,
    				Material.SCULK_SENSOR, Material.CHISELED_BOOKSHELF, Material.DECORATED_POT,
    				Material.SUSPICIOUS_SAND,
    				
    				//Material.SUSPICIOUS_GRAVEL, Material.TRIAL_SPAWNER, Material.CRAFTER, Material.VAULT
    				
    		});
    static final CraftMetaItem.ItemMetaKeyType<NbtComponent> BLOCK_ENTITY_TAG;
    
    static final CraftMetaItem.ItemMetaKey BLOCK_ENTITY_TAG_CUSTOM_DATA = new CraftMetaItem.ItemMetaKey("block-entity-tag");
    static final CraftMetaItem.ItemMetaKey BLOCK_ENTITY_COMPONENTS = new CraftMetaItem.ItemMetaKey("block-entity-components");
    
    final Material material;
    //private CardboardBlockEntityState<?> blockEntityTag;
    NbtComponent blockEntityTag;
    private NbtCompound internalTag;
    
    ComponentMap components;
    
    private Material materialForBlockEntityType() {
        return this.material;
    }

    CraftMetaBlockState(CraftMetaItem meta, Material material) {
        super(meta);
        this.material = material;
        if (!(meta instanceof CraftMetaBlockState) || ((CraftMetaBlockState)meta).material != material) {
            this.blockEntityTag = null;
            this.components = ComponentMap.EMPTY;
            return;
        }
        CraftMetaBlockState te = (CraftMetaBlockState)meta;
        this.components = te.components;
        this.blockEntityTag = te.blockEntityTag;
    }
    
    CraftMetaBlockState(ComponentChanges tag, Material material, Set<ComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        this.components = this.components != null ? this.components : ComponentMap.EMPTY;
        this.blockEntityTag = this.blockEntityTag != null ? this.blockEntityTag : NbtComponent.DEFAULT;
        this.material = material;
        this.updateBlockState(tag);
    }
    
    private void updateBlockState(ComponentChanges tag) {
        CraftMetaBlockState.getOrEmpty(tag, BLOCK_ENTITY_TAG).ifPresent(nbt -> {
            this.blockEntityTag = nbt;
        });
        if (!tag.isEmpty()) {
            ComponentMap.Builder map = ComponentMap.builder();
            BlockEntity dummyBlockEntity = Objects.requireNonNull(CraftBlockStates.createNewBlockEntity(this.materialForBlockEntityType()));
            Set<ComponentType<?>> applied = ((IMixinBlockEntity)dummyBlockEntity).applyComponentsSet(ComponentMap.EMPTY, ComponentChanges.EMPTY);
            for (ComponentType<?> seen : applied) {
                ((IComponentChanges)this.unhandledTags).clear(seen);
            }
            if (!applied.isEmpty()) {
                for (ComponentType type : applied) {
                    if (CraftMetaItem.DEFAULT_HANDLED_DCTS.contains(type)) continue;
                    CraftMetaBlockState.getOrEmpty(tag, type).ifPresent(value -> map.add(type, value));
                }
            }
            this.components = map.build();
        }
    }

    /*
    CraftMetaBlockState(ComponentChanges tag, Material material, Set<ComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        this.material = material;
        
        this.components = this.components != null ? this.components : ComponentMap.EMPTY;
        
        CraftMetaBlockState.getOrEmpty(tag, BLOCK_ENTITY_TAG).ifPresent(nbt -> {
            this.blockEntityTag = CraftMetaBlockState.getBlockState(material, nbt.copyNbt());
        });
        if (!tag.isEmpty()) {
            if (this.blockEntityTag == null) {
                this.blockEntityTag = CraftMetaBlockState.getBlockState(material, null);
            }
            MergedComponentMap map = new MergedComponentMap(ComponentMap.EMPTY);
            map.applyChanges(tag);
            TrackedDataComponentMap track = new TrackedDataComponentMap(map);
            this.blockEntityTag.applyComponents(track, tag);
            for (ComponentType<?> seen : track.seen) {
                // TODO: 1.20.6
            	// this.unhandledTags.clear(seen);
            }
        }
    }
    */

    /*
    CraftMetaBlockState(Map<String, Object> map) {
        super(map);
        String matName = org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getString(map, "blockMaterial", true);
        Material m = Material.getMaterial((String)matName);
        this.material = m != null ? m : Material.AIR;
        this.blockEntityTag = CraftMetaBlockState.getBlockState(this.material, this.internalTag);
        this.components = this.components != null ? this.components : ComponentMap.EMPTY;
        this.internalTag = null;
    }
    */
    
    CraftMetaBlockState(Map<String, Object> map) {
        super(map);
        BlockVector legacyPosition;
        this.components = this.components != null ? this.components : ComponentMap.EMPTY;
        this.blockEntityTag = this.blockEntityTag != null ? this.blockEntityTag : NbtComponent.DEFAULT;
        String blockMaterial = SerializableMeta.getString(map, "blockMaterial", true);
        Material material = Material.getMaterial((String)blockMaterial);
        Material material2 = this.material = material != null ? material : Material.AIR;
        if (this.internalTag != null) {
            this.setBlockState(CraftMetaBlockState.getBlockState(this.material, this.internalTag));
            this.internalTag = null;
        }
        if ((legacyPosition = SerializableMeta.getObject(BlockVector.class, map, "blockPosition", true)) != null) {
            this.blockEntityTag = this.blockEntityTag.apply(blockEntityTag -> {
                try (ErrorReporter.Logging problemReporter = new ErrorReporter.Logging(() -> "blockEntityTag", CraftMetaItem.LOGGER);){
                    NbtWriteView output = NbtWriteView_createWrappingWithContext(problemReporter, CraftRegistry.getMinecraftRegistry(), blockEntityTag);
                    if (blockEntityTag.isEmpty()) {
                        BlockEntity.writeId(output, Objects.requireNonNull(CraftBlockStates.getBlockEntityType(this.materialForBlockEntityType())));
                    }
                    output.putInt("x", legacyPosition.getBlockX());
                    output.putInt("y", legacyPosition.getBlockY());
                    output.putInt("z", legacyPosition.getBlockZ());
                }
            });
        }
    }

    /*
    @Override
    void applyToItem(CraftMetaItem.Applicator tag) {
        super.applyToItem(tag);
        if (this.blockEntityTag != null) {
            NbtCompound nbt = this.blockEntityTag.getSnapshotCustomNbtOnly();
            nbt.remove("id");
            if (!nbt.isEmpty()) {
                BlockEntity.writeIdToNbt(nbt, ((BlockEntity)this.blockEntityTag.getTileEntity()).getType());
                tag.put(BLOCK_ENTITY_TAG, NbtComponent.of(nbt));
            }
            for (Component<?> component : this.blockEntityTag.collectComponents()) {
                tag.builder.add(component);
            }
        }
    }
    */
    
    @Override
    void applyToItem(CraftMetaItem.Applicator tag) {
        super.applyToItem(tag);
        NbtCompound nbt = this.blockEntityTag.copyNbt();
        if (!nbt.isEmpty()) {
            if (nbt.getString("id").isEmpty()) {
                try (ErrorReporter.Logging problemReporter = new ErrorReporter.Logging(() -> "CraftMetaBlockState#apply", CraftMetaItem.LOGGER);){
                    BlockEntity.writeId(NbtWriteView_createWrappingWithContext(problemReporter, CraftRegistry.getMinecraftRegistry(), nbt), Objects.requireNonNull(CraftBlockStates.getBlockEntityType(this.materialForBlockEntityType())));
                    
                }
            }
            tag.put(BLOCK_ENTITY_TAG, NbtComponent.of(nbt));
        }
        for (Component<?> component : this.components) {
            if (CraftMetaItem.DEFAULT_HANDLED_DCTS.contains(component.type())) continue;
            tag.builder.add(component);
        }
    }
    
    public static NbtWriteView NbtWriteView_createWrappingWithContext(
			final ErrorReporter.Logging problemReporter,
			final DynamicRegistryManager lookup,
			final NbtCompound compoundTag
		) {
            return new NbtWriteView(problemReporter, lookup.getOps(NbtOps.INSTANCE), compoundTag);
        }

    /*
    @Override
    void deserializeInternal(NbtCompound tag, Object context) {
        super.deserializeInternal(tag, context);
        if (tag.contains(CraftMetaBlockState.BLOCK_ENTITY_TAG.NBT, 10)) {
            this.internalTag = tag.getCompound(CraftMetaBlockState.BLOCK_ENTITY_TAG.NBT);
        }
    }
    */
    
    @Override
    void deserializeInternal(NbtCompound tag, Object context) {
        super.deserializeInternal(tag, context);
        Optional<NbtCompound> blockEntityTag = tag.getCompound(CraftMetaBlockState.BLOCK_ENTITY_TAG.NBT);
        if (blockEntityTag.isPresent()) {
            this.internalTag = blockEntityTag.get();
            return;
        }
        tag.getCompound(CraftMetaBlockState.BLOCK_ENTITY_TAG_CUSTOM_DATA.NBT).ifPresent(blockEntityCustomTag -> {
            this.blockEntityTag = NbtComponent.of(blockEntityCustomTag);
        });
        tag.getCompound(CraftMetaBlockState.BLOCK_ENTITY_COMPONENTS.NBT).ifPresent(components -> {
            this.components = (ComponentMap)ComponentMap.CODEC.parse(CraftRegistry.getMinecraftRegistry().getOps(NbtOps.INSTANCE), components).getOrThrow();
        });
    }

    /*
    @Override
    void serializeInternal(Map<String, NbtElement> internalTags) {
        if (this.blockEntityTag != null) {
            internalTags.put(CraftMetaBlockState.BLOCK_ENTITY_TAG.NBT, this.blockEntityTag.getSnapshotNBT());
        }
    }
    */
    
    @Override
    void serializeInternal(Map<String, NbtElement> internalTags) {
        if (!this.blockEntityTag.isEmpty()) {
            internalTags.put(CraftMetaBlockState.BLOCK_ENTITY_TAG_CUSTOM_DATA.NBT, this.blockEntityTag.getNbt());
        }
        if (!this.components.isEmpty()) {
            NbtElement componentsTag = ComponentMap.CODEC.encodeStart(CraftRegistry.getMinecraftRegistry().getOps(NbtOps.INSTANCE), this.components).getOrThrow();
            internalTags.put(CraftMetaBlockState.BLOCK_ENTITY_COMPONENTS.NBT, componentsTag);
        }
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        builder.put("blockMaterial", this.material.name());
        return builder;
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.blockEntityTag != null) {
            hash = 61 * hash + this.blockEntityTag.hashCode();
        }
        return original != hash ? CraftMetaBlockState.class.hashCode() ^ hash : hash;
    }

    @Override
    public boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaBlockState) {
            CraftMetaBlockState that = (CraftMetaBlockState)meta;
            return Objects.equals(this.blockEntityTag, that.blockEntityTag);
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaBlockState || this.blockEntityTag == null);
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.blockEntityTag == null;
    }

    @Override
    boolean applicableTo(Material type) {
        return BLOCK_STATE_MATERIALS.contains(type);
    }

    @Override
    public CraftMetaBlockState clone() {
        CraftMetaBlockState meta = (CraftMetaBlockState)super.clone();
        meta.blockEntityTag = this.blockEntityTag;
        meta.components = this.components;
        return meta;
    }

    public boolean hasBlockState() {
        return this.blockEntityTag != null;
    }

    public void clearBlockState() {
        this.blockEntityTag = null;
    }

    /*
    public BlockState getBlockState() {
        return this.blockEntityTag != null ? this.blockEntityTag.copy() : CraftMetaBlockState.getBlockState(this.material, null);
    }
    */
    
    public CardboardBlockEntityState<?> getBlockState() {
        BlockPos pos = BlockPos.ORIGIN;
        Material stateMaterial = this.materialForBlockEntityType();
        if (!this.blockEntityTag.isEmpty()) {
            pos = BlockEntity.posFromNbt(null, this.blockEntityTag.getNbt());
        }
        BlockEntityType<?> type = Objects.requireNonNull(CraftBlockStates.getBlockEntityType(stateMaterial));
        net.minecraft.block.BlockState nmsBlockState = ((CraftBlockData)this.getBlockData(stateMaterial)).getState();
        BlockEntity blockEntity = (BlockEntity)Objects.requireNonNull(type.instantiate(pos, nmsBlockState));
        if (!this.blockEntityTag.isEmpty()) {
            this.blockEntityTag.applyToBlockEntity(blockEntity, CraftRegistry.getMinecraftRegistry());
        }
        MergedComponentMap patchedMap = new MergedComponentMap(nmsBlockState.getBlock().asItem().getComponents());
        patchedMap.setAll(this.components);
        Applicator applicator = new Applicator(){};
        super.applyToItem(applicator);
        patchedMap.applyChanges(applicator.build());
        blockEntity.readComponents(nmsBlockState.getBlock().asItem().getComponents(), patchedMap.getChanges());
        return (CardboardBlockEntityState)CraftBlockStates.getBlockState(null, pos, nmsBlockState, blockEntity);
    }

    /*
    private static CardboardBlockEntityState<?> getBlockState(Material material, NbtCompound blockEntityTag) {
        Material stateMaterial;
        BlockPos pos = BlockPos.ORIGIN;
        Material material2 = stateMaterial = material != Material.SHIELD ? material : CraftMetaBlockState.shieldToBannerHack();
        if (blockEntityTag != null) {
            if (material == Material.SHIELD) {
                blockEntityTag.putString("id", "minecraft:banner");
            } else if (material == Material.BEE_NEST || material == Material.BEEHIVE) {
                blockEntityTag.putString("id", "minecraft:beehive");
            } else if (SHULKER_BOX_MATERIALS.contains(material)) {
                blockEntityTag.putString("id", "minecraft:shulker_box");
            }
            pos = BlockEntity.posFromNbt(blockEntityTag);
        }
        
        
        return (CardboardBlockEntityState)CraftBlockStates.getBlockState(pos, stateMaterial, blockEntityTag);
    }
    */
    
    private static CardboardBlockEntityState<?> getBlockState(Material material, NbtCompound blockEntityTag) {
        Material stateMaterial;
        BlockPos pos = BlockPos.ORIGIN;
        Material material2 = stateMaterial = material != Material.SHIELD ? material : CraftMetaBlockState.shieldToBannerHack(blockEntityTag);
        if (blockEntityTag != null) {
            if (material == Material.SHIELD) {
                blockEntityTag.putString("id", "minecraft:banner");
            } else if (material == Material.BEE_NEST || material == Material.BEEHIVE) {
                blockEntityTag.putString("id", "minecraft:beehive");
            } else if (SHULKER_BOX_MATERIALS.contains(material)) {
                blockEntityTag.putString("id", "minecraft:shulker_box");
            }
            pos = BlockEntity.posFromNbt(null, blockEntityTag);
        }
        return (CardboardBlockEntityState)CraftBlockStates.getBlockState(CraftRegistry.getMinecraftRegistry(), pos, stateMaterial, blockEntityTag);
    }

    /*
    public void setBlockState(BlockState blockState) {
        Preconditions.checkArgument((blockState != null ? 1 : 0) != 0, (Object)"blockState must not be null");
        Material stateMaterial = this.material != Material.SHIELD ? this.material : CraftMetaBlockState.shieldToBannerHack();
        Class<? extends CraftBlockState> blockStateType = CraftBlockStates.getBlockStateType(stateMaterial);
        Preconditions.checkArgument((blockStateType == blockState.getClass() && blockState instanceof CardboardBlockEntityState ? 1 : 0) != 0, (Object)("Invalid blockState for " + String.valueOf(this.material)));
        this.blockEntityTag = (CardboardBlockEntityState)blockState;
    }
    */
    
    public void setBlockState(BlockState blockState) {
        Preconditions.checkArgument((blockState != null ? 1 : 0) != 0, (Object)"blockState must not be null");
        Material stateMaterial = this.material != Material.SHIELD ? this.material : CraftMetaBlockState.shieldToBannerHack(null);
        Class<? extends CraftBlockState> blockStateType = CraftBlockStates.getBlockStateType(stateMaterial);
        Preconditions.checkArgument((blockStateType == blockState.getClass() && blockState instanceof CardboardBlockEntityState ? 1 : 0) != 0, (String)"Invalid blockState for %s", (Object)this.material);
        CardboardBlockEntityState craftBlockState = (CardboardBlockEntityState)blockState;
        NbtCompound data = craftBlockState.getSnapshotCustomNbtOnly();
        MergedComponentMap patchedMap = new MergedComponentMap(craftBlockState.getHandle().getBlock().asItem().getComponents());
        ComponentMap map = craftBlockState.collectComponents();
        patchedMap.setAll(map);
        if (!data.isEmpty()) {
            patchedMap.set(CraftMetaBlockState.BLOCK_ENTITY_TAG.TYPE, NbtComponent.of(data));
        }
        ComponentChanges patch = patchedMap.getChanges();
        this.updateFromPatch(patch, null);
        this.blockEntityTag = NbtComponent.DEFAULT;
        this.components = ComponentMap.EMPTY;
        this.updateBlockState(patch);
    }

    @Deprecated
    private static Material shieldToBannerHack() {
        return Material.WHITE_BANNER;
    }
    
    private static Material shieldToBannerHack(NbtCompound tag) {
        Optional baseColor;
        if (tag != null && (baseColor = tag.getCompound("components").flatMap(components -> components.getString("minecraft:base_color"))).isPresent()) {
            DyeColor color = DyeColor.getByWoolData((byte)((byte)net.minecraft.util.DyeColor.byId((String)baseColor.get(), net.minecraft.util.DyeColor.WHITE).getIndex()));
            return CraftMetaShield.shieldToBannerHack(color);
        }
        return Material.WHITE_BANNER;
    }

    static {
        BLOCK_STATE_MATERIALS.addAll(SHULKER_BOX_MATERIALS);
        BLOCK_ENTITY_TAG = new CraftMetaItem.ItemMetaKeyType<NbtComponent>(DataComponentTypes.BLOCK_ENTITY_DATA, "BlockEntityTag");
    }

    private static final class TrackedDataComponentMap
    implements ComponentMap {
        private final Set<ComponentType<?>> seen = new HashSet();
        private final ComponentMap handle;

        public TrackedDataComponentMap(ComponentMap map) {
            this.handle = map;
        }

        @Override
        public <T> T get(ComponentType<? extends T> type) {
            this.seen.add(type);
            return this.handle.get(type);
        }

        @Override
        public Set<ComponentType<?>> getTypes() {
            return this.handle.getTypes();
        }

        @Override
        public Iterator<Component<?>> iterator() {
            return this.handle.iterator();
        }
    }
}


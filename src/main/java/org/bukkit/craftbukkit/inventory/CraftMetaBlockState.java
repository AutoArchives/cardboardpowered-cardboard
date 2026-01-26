package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.util.BlockVector;
import org.cardboardpowered.TypedEntityDataExtra;
import org.cardboardpowered.impl.block.CardboardBlockEntityState;
import org.cardboardpowered.interfaces.IComponentChanges;
import org.cardboardpowered.bridge.world.level.block.entity.BlockEntityBridge;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaBlockState extends CraftMetaItem implements BlockStateMeta {
	
	// cardboard start
	 public static TagValueOutput NbtWriteView_createWrappingWithContext(
				final ProblemReporter.ScopedCollector problemReporter,
				final RegistryAccess lookup,
				final CompoundTag compoundTag
			) {
	            return new TagValueOutput(problemReporter, lookup.createSerializationContext(NbtOps.INSTANCE), compoundTag);
	        }

	// cardboard end
	
   private static final Set<Material> SHULKER_BOX_MATERIALS = Sets.newHashSet(
      new Material[]{
         Material.SHULKER_BOX,
         Material.WHITE_SHULKER_BOX,
         Material.ORANGE_SHULKER_BOX,
         Material.MAGENTA_SHULKER_BOX,
         Material.LIGHT_BLUE_SHULKER_BOX,
         Material.YELLOW_SHULKER_BOX,
         Material.LIME_SHULKER_BOX,
         Material.PINK_SHULKER_BOX,
         Material.GRAY_SHULKER_BOX,
         Material.LIGHT_GRAY_SHULKER_BOX,
         Material.CYAN_SHULKER_BOX,
         Material.PURPLE_SHULKER_BOX,
         Material.BLUE_SHULKER_BOX,
         Material.BROWN_SHULKER_BOX,
         Material.GREEN_SHULKER_BOX,
         Material.RED_SHULKER_BOX,
         Material.BLACK_SHULKER_BOX
      }
   );
   static final CraftMetaItem.ItemMetaKeyType<TypedEntityData<BlockEntityType<?>>> BLOCK_ENTITY_TAG = new CraftMetaItem.ItemMetaKeyType<>(
      DataComponents.BLOCK_ENTITY_DATA, "BlockEntityTag"
   );
   static final CraftMetaItem.ItemMetaKey BLOCK_ENTITY_TAG_CUSTOM_DATA = new CraftMetaItem.ItemMetaKey("block-entity-tag");
   static final CraftMetaItem.ItemMetaKey BLOCK_ENTITY_COMPONENTS = new CraftMetaItem.ItemMetaKey("block-entity-components");
   private static final CompoundTag EMPTY_TAG = new CompoundTag();
   final Material material;
   DataComponentMap components;
   CompoundTag blockEntityTag;
   private CompoundTag internalTag;

   private Material materialForBlockEntityType() {
      return this.material;
   }

   CraftMetaBlockState(CraftMetaItem meta, Material material) {
      super(meta);
      this.components = this.components != null ? this.components : DataComponentMap.EMPTY;
      this.blockEntityTag = this.blockEntityTag != null ? this.blockEntityTag : EMPTY_TAG;
      this.material = material;
      if (meta instanceof CraftMetaBlockState metaBlockState && metaBlockState.material == material) {
         this.components = metaBlockState.components;
         this.blockEntityTag = metaBlockState.blockEntityTag;
      } else {
         this.components = DataComponentMap.EMPTY;
         this.blockEntityTag = EMPTY_TAG;
      }
   }

   CraftMetaBlockState(DataComponentPatch tag, Material material, Set<DataComponentType<?>> extraHandledDcts) {
      super(tag, extraHandledDcts);
      this.components = this.components != null ? this.components : DataComponentMap.EMPTY;
      this.blockEntityTag = this.blockEntityTag != null ? this.blockEntityTag : EMPTY_TAG;
      this.material = material;
      this.updateBlockState(tag);
   }

   private void updateBlockState(DataComponentPatch tag) {

      getOrEmpty(tag, BLOCK_ENTITY_TAG).ifPresent(nbt -> this.blockEntityTag = TypedEntityDataExtra.copyTagWithBlockEntityId(nbt));
      if (!tag.isEmpty()) {
         DataComponentMap.Builder map = DataComponentMap.builder();
         BlockEntity dummyBlockEntity = Objects.requireNonNull(CraftBlockStates.createNewBlockEntity(this.materialForBlockEntityType()));
         Set<DataComponentType<?>> applied = ( (BlockEntityBridge) dummyBlockEntity).applyComponentsSet(DataComponentMap.EMPTY, DataComponentPatch.EMPTY);

         for (DataComponentType<?> seen : applied) {
        	 ((IComponentChanges)super.unhandledTags).clear(seen);
         }

         if (!applied.isEmpty()) {
            for (DataComponentType type : applied) {
               if (!CraftMetaItem.DEFAULT_HANDLED_DCTS.contains(type)) {
                  getOrEmpty(tag, type).ifPresent(value -> map.set(type, value));
               }
            }
         }

         this.components = map.build();
      }
   }

   CraftMetaBlockState(Map<String, Object> map) {
      super(map);
      this.components = this.components != null ? this.components : DataComponentMap.EMPTY;
      this.blockEntityTag = this.blockEntityTag != null ? this.blockEntityTag : EMPTY_TAG;
      String blockMaterial = SerializableMeta.getString(map, "blockMaterial", true);
      Material material = Material.getMaterial(blockMaterial);
      this.material = material != null ? material : Material.AIR;
      if (this.internalTag != null) {
         this.setBlockState(getBlockState(this.material, this.internalTag));
         this.internalTag = null;
      }

      BlockVector legacyPosition = SerializableMeta.getObject(BlockVector.class, map, "blockPosition", true);
      if (legacyPosition != null) {
         CompoundTag blockEntityTag = this.blockEntityTag.copy();

         try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(() -> "blockEntityTag", CraftMetaItem.LOGGER)) {
            TagValueOutput output = NbtWriteView_createWrappingWithContext(problemReporter, CraftRegistry.getMinecraftRegistry(), blockEntityTag);
            if (blockEntityTag.isEmpty()) {
               BlockEntity.addEntityType(output, Objects.requireNonNull(CraftBlockStates.getBlockEntityType(this.materialForBlockEntityType())));
            }

            output.putInt("x", legacyPosition.getBlockX());
            output.putInt("y", legacyPosition.getBlockY());
            output.putInt("z", legacyPosition.getBlockZ());
         }

         this.blockEntityTag = blockEntityTag;
      }
   }

   @Override
   void applyToItem(CraftMetaItem.Applicator tag) {
      super.applyToItem(tag);
      CompoundTag nbt = this.blockEntityTag.copy();
      if (!nbt.isEmpty()) {
         if (nbt.getString("id").isEmpty()) {
            try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(() -> "CraftMetaBlockState#apply", CraftMetaItem.LOGGER)) {
               BlockEntity.addEntityType(
            		   NbtWriteView_createWrappingWithContext(problemReporter, CraftRegistry.getMinecraftRegistry(), nbt),
                  Objects.requireNonNull(CraftBlockStates.getBlockEntityType(this.materialForBlockEntityType()))
               );
            }
         }

         tag.put(BLOCK_ENTITY_TAG, TypedEntityDataExtra.decodeBlockEntity(nbt));
      }

      for (TypedDataComponent<?> component : this.components) {
         if (!CraftMetaItem.DEFAULT_HANDLED_DCTS.contains(component.type())) {
            tag.builder.set(component);
         }
      }
   }

   @Override
   void deserializeInternal(CompoundTag tag, Object context) {
      super.deserializeInternal(tag, context);
      Optional<CompoundTag> blockEntityTag = tag.getCompound(BLOCK_ENTITY_TAG.NBT);
      if (blockEntityTag.isPresent()) {
         this.internalTag = blockEntityTag.get();
      } else {
         tag.getCompound(BLOCK_ENTITY_TAG_CUSTOM_DATA.NBT).ifPresent(blockEntityCustomTag -> this.blockEntityTag = blockEntityCustomTag.copy());
         tag.getCompound(BLOCK_ENTITY_COMPONENTS.NBT)
            .ifPresent(
               components -> this.components = (DataComponentMap)DataComponentMap.CODEC
                  .parse(CraftRegistry.getMinecraftRegistry().createSerializationContext(NbtOps.INSTANCE), components)
                  .getOrThrow()
            );
      }
   }

   @Override
   void serializeInternal(Map<String, Tag> internalTags) {
      if (!this.blockEntityTag.isEmpty()) {
         internalTags.put(BLOCK_ENTITY_TAG_CUSTOM_DATA.NBT, this.blockEntityTag);
      }

      if (!this.components.isEmpty()) {
         Tag componentsTag = (Tag)DataComponentMap.CODEC
            .encodeStart(CraftRegistry.getMinecraftRegistry().createSerializationContext(NbtOps.INSTANCE), this.components)
            .getOrThrow();
         internalTags.put(BLOCK_ENTITY_COMPONENTS.NBT, componentsTag);
      }
   }

   @Override
   Builder<String, Object> serialize(Builder<String, Object> builder) {
      super.serialize(builder);
      builder.put("blockMaterial", this.material.name());
      return builder;
   }

   @Override
   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      hash = 61 * hash + this.blockEntityTag.hashCode();
      hash = 61 * hash + this.components.hashCode();
      return original != hash ? CraftMetaBlockState.class.hashCode() ^ hash : hash;
   }

   @Override
   public boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else {
         return !(meta instanceof CraftMetaBlockState other)
            ? true
            : Objects.equals(this.blockEntityTag, other.blockEntityTag) && Objects.equals(this.components, other.components);
      }
   }

   boolean isBlockStateEmpty() {
      return this.blockEntityTag == null;
   }

   @Override
   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaBlockState || this.blockEntityTag.isEmpty() && this.components.isEmpty());
   }

   @Override
   boolean isEmpty() {
      return super.isEmpty() && this.blockEntityTag.isEmpty() && this.components.isEmpty();
   }

   public CraftMetaBlockState clone() {
      CraftMetaBlockState meta = (CraftMetaBlockState)super.clone();
      meta.blockEntityTag = this.blockEntityTag;
      meta.components = this.components;
      return meta;
   }

   public boolean hasBlockState() {
      return !this.blockEntityTag.isEmpty() || !this.components.isEmpty();
   }

   public void clearBlockState() {
      this.blockEntityTag = EMPTY_TAG;
      this.components = DataComponentMap.EMPTY;
   }

   public CardboardBlockEntityState<?> getBlockState() {
      BlockPos pos = BlockPos.ZERO;
      Material stateMaterial = this.materialForBlockEntityType();
      if (!this.blockEntityTag.isEmpty()) {
         pos = BlockEntity.getPosFromTag(null, this.blockEntityTag);
      }

      BlockEntityType<?> type = Objects.requireNonNull(CraftBlockStates.getBlockEntityType(stateMaterial));
      BlockState nmsBlockState = ((CraftBlockData)this.getBlockData(stateMaterial)).getState();
      BlockEntity blockEntity = Objects.requireNonNull(type.create(pos, nmsBlockState));
      if (!this.blockEntityTag.isEmpty()) {
    	  TypedEntityDataExtra.decodeBlockEntity(this.blockEntityTag).loadInto(blockEntity, CraftRegistry.getMinecraftRegistry());
      }

      PatchedDataComponentMap patchedMap = new PatchedDataComponentMap(nmsBlockState.getBlock().asItem().components());
      patchedMap.setAll(this.components);
      CraftMetaItem.Applicator applicator = new CraftMetaItem.Applicator() {};
      super.applyToItem(applicator);
      patchedMap.applyPatch(applicator.build());
      blockEntity.applyComponents(nmsBlockState.getBlock().asItem().components(), patchedMap.asPatch());
      return (CardboardBlockEntityState<?>)CraftBlockStates.getBlockState(null, pos, nmsBlockState, blockEntity);
   }

   private static CardboardBlockEntityState<?> getBlockState(Material material, CompoundTag blockEntityTag) {
      BlockPos pos = BlockPos.ZERO;
      Material stateMaterial = material != Material.SHIELD ? material : shieldToBannerHack(blockEntityTag);
      if (blockEntityTag != null) {
         if (material == Material.SHIELD) {
            blockEntityTag.putString("id", "minecraft:banner");
         } else if (material == Material.BEE_NEST || material == Material.BEEHIVE) {
            blockEntityTag.putString("id", "minecraft:beehive");
         } else if (SHULKER_BOX_MATERIALS.contains(material)) {
            blockEntityTag.putString("id", "minecraft:shulker_box");
         }

         pos = BlockEntity.getPosFromTag(null, blockEntityTag);
      }

      return (CardboardBlockEntityState<?>)CraftBlockStates.getBlockState(CraftRegistry.getMinecraftRegistry(), pos, stateMaterial, blockEntityTag);
   }

   public void setBlockState(org.bukkit.block.BlockState blockState) {
      Preconditions.checkArgument(blockState != null, "blockState must not be null");
      Material stateMaterial = this.material != Material.SHIELD ? this.material : shieldToBannerHack(null);
      Class<?> blockStateType = CraftBlockStates.getBlockStateType(stateMaterial);
      Preconditions.checkArgument(
         blockStateType == blockState.getClass() && blockState instanceof CardboardBlockEntityState, "Invalid blockState for %s", this.material
      );
      CardboardBlockEntityState<?> craftBlockState = (CardboardBlockEntityState<?>)blockState;
      CompoundTag data = craftBlockState.getSnapshotCustomNbtOnly();
      PatchedDataComponentMap patchedMap = new PatchedDataComponentMap(craftBlockState.getHandle().getBlock().asItem().components());
      DataComponentMap map = craftBlockState.collectComponents();
      patchedMap.setAll(map);
      if (!data.isEmpty()) {
         patchedMap.set(BLOCK_ENTITY_TAG.TYPE, TypedEntityDataExtra.decodeBlockEntity(data));
      }

      DataComponentPatch patch = patchedMap.asPatch();
      this.updateFromPatch(patch, null);
      this.blockEntityTag = EMPTY_TAG;
      this.components = DataComponentMap.EMPTY;
      this.updateBlockState(patch);
   }

   private static Material shieldToBannerHack(CompoundTag tag) {
      if (tag != null) {
         Optional<String> baseColor = tag.getCompound("components").flatMap(components -> components.getString("minecraft:base_color"));
         if (baseColor.isPresent()) {
            DyeColor color = DyeColor.getByWoolData((byte)net.minecraft.world.item.DyeColor.byName(baseColor.get(), net.minecraft.world.item.DyeColor.WHITE).getId());
            return CraftMetaShield.shieldToBannerHack(color);
         }
      }

      return Material.WHITE_BANNER;
   }
}

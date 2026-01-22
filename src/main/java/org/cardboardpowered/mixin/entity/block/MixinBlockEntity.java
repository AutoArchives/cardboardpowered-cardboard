package org.cardboardpowered.mixin.entity.block;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.cardboardpowered.interfaces.IMixinBlockEntity;
import org.cardboardpowered.interfaces.IMixinWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

@Mixin(BlockEntity.class)
public class MixinBlockEntity implements IMixinBlockEntity {

    private static final CraftPersistentDataTypeRegistry DATA_TYPE_REGISTRY = new CraftPersistentDataTypeRegistry();
    public CraftPersistentDataContainer persistentDataContainer;

    @Shadow
    private DataComponentMap components = DataComponentMap.EMPTY;
    
    @Shadow public Level level;
    @Shadow public BlockPos worldPosition;

    @Override
    public CraftPersistentDataContainer getPersistentDataContainer() {
        return persistentDataContainer;
    }

    @Override
    public InventoryHolder getOwner_() {
        return getOwner(true);
    }

    public InventoryHolder getOwner(boolean useSnapshot) {
        if (level == null) return null;

        org.bukkit.block.Block block = ((IMixinWorld)this.level).getCraftWorld().getBlockAt(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        if (block == null) {
            org.bukkit.Bukkit.getLogger().warning("No block for owner at " + level + ", pos: " + worldPosition);
            return null;
        }
        org.bukkit.block.BlockState state = block.getState(useSnapshot); // Paper: useSnapshot
        if (state instanceof InventoryHolder) return (InventoryHolder) state;
        System.out.println("STATE NOT INSTANCEOF INVENTORYHOLDER!!");
        return null;
    }

    @Override
    public void setCardboardPersistentDataContainer(CraftPersistentDataContainer c) {
        this.persistentDataContainer = c;
    }

    @Override
    public CraftPersistentDataTypeRegistry getCardboardDTR() {
        return DATA_TYPE_REGISTRY;
    }
    
    @Shadow
    public void applyImplicitComponents(DataComponentGetter components) {
	}
    
    @Override
    public Set<DataComponentType<?>> applyComponentsSet(DataComponentMap defaultComponents, DataComponentPatch components) {
		final Set<DataComponentType<?>> set = new HashSet<>();
		set.add(DataComponents.BLOCK_ENTITY_DATA);
		set.add(DataComponents.BLOCK_STATE);
		final DataComponentMap componentMap = PatchedDataComponentMap.fromPatch(defaultComponents, components);
		this.applyImplicitComponents(new DataComponentGetter() {

			@Override
			public <T> T get(DataComponentType<? extends T> type) {
				set.add(type);
				return componentMap.get(type);
			}

			@Override
			public <T> T getOrDefault(DataComponentType<? extends T> type, T fallback) {
				set.add(type);
				return componentMap.getOrDefault(type, fallback);
			}
		});
		DataComponentPatch componentChanges = components.forget(set::contains);
		this.components = componentChanges.split().added();
		
		// Paper - start
		set.remove(DataComponents.BLOCK_ENTITY_DATA); // Remove as never actually added by applyImplicitComponents
		return set;
		// Paper - end
	}

}
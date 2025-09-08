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

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(BlockEntity.class)
public class MixinBlockEntity implements IMixinBlockEntity {

    private static final CraftPersistentDataTypeRegistry DATA_TYPE_REGISTRY = new CraftPersistentDataTypeRegistry();
    public CraftPersistentDataContainer persistentDataContainer;

    @Shadow
    private ComponentMap components = ComponentMap.EMPTY;
    
    @Shadow public World world;
    @Shadow public BlockPos pos;

    @Override
    public CraftPersistentDataContainer getPersistentDataContainer() {
        return persistentDataContainer;
    }

    @Override
    public InventoryHolder getOwner_() {
        return getOwner(true);
    }

    public InventoryHolder getOwner(boolean useSnapshot) {
        if (world == null) return null;

        org.bukkit.block.Block block = ((IMixinWorld)this.world).getCraftWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        if (block == null) {
            org.bukkit.Bukkit.getLogger().warning("No block for owner at " + world + ", pos: " + pos);
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
    public void readComponents(ComponentsAccess components) {
	}
    
    @Override
    public Set<ComponentType<?>> applyComponentsSet(ComponentMap defaultComponents, ComponentChanges components) {
		final Set<ComponentType<?>> set = new HashSet<>();
		set.add(DataComponentTypes.BLOCK_ENTITY_DATA);
		set.add(DataComponentTypes.BLOCK_STATE);
		final ComponentMap componentMap = MergedComponentMap.create(defaultComponents, components);
		this.readComponents(new ComponentsAccess() {

			@Override
			public <T> T get(ComponentType<? extends T> type) {
				set.add(type);
				return componentMap.get(type);
			}

			@Override
			public <T> T getOrDefault(ComponentType<? extends T> type, T fallback) {
				set.add(type);
				return componentMap.getOrDefault(type, fallback);
			}
		});
		ComponentChanges componentChanges = components.withRemovedIf(set::contains);
		this.components = componentChanges.toAddedRemovedPair().added();
		
		// Paper - start
		set.remove(DataComponentTypes.BLOCK_ENTITY_DATA); // Remove as never actually added by applyImplicitComponents
		return set;
		// Paper - end
	}

}
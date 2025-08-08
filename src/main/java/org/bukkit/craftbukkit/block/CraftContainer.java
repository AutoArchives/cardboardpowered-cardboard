package org.bukkit.craftbukkit.block;

import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.inventory.ItemStack;
import org.cardboardpowered.impl.block.CardboardBlockEntityState;

public abstract class CraftContainer<T extends LockableContainerBlockEntity> extends CardboardBlockEntityState<T> implements Container {

    public CraftContainer(World world, T tileEntity) {
        super(world, tileEntity);
    }

    protected CraftContainer(CraftContainer<T> state, Location location) {
        super(state, location);
    }
    
    @Override
    public abstract CraftContainer<T> copy();

    @Override
    public abstract CraftContainer<T> copy(Location var1);
	
    /*
    public CraftContainer(Block block, Class<T> tileEntityClass) {
        super(block, tileEntityClass);
    }

    public CraftContainer(final Material material, T tileEntity) {
        super(material, tileEntity);
    }
    */

    @Override
    public boolean isLocked() {
    	return this.getSnapshot().lock != ContainerLock.EMPTY;
    }

    @Override
    public String getLock() {
    	Optional<? extends Text> customName = this.getSnapshot().lock.predicate().components().exact().toChanges().get(DataComponentTypes.CUSTOM_NAME);

        return (customName != null) ? customName.map(CraftChatMessage::fromComponent).orElse("") : "";
    }

    @Override
    public void setLock(String key) {
    	if (key == null) {
            this.getSnapshot().lock = ContainerLock.EMPTY;
        } else {
        	ComponentMapPredicate predicate = ComponentMapPredicate.builder().add(DataComponentTypes.CUSTOM_NAME, CraftChatMessage.fromStringOrNull(key)).build();
            this.getSnapshot().lock = new ContainerLock(new ItemPredicate(Optional.empty(), NumberRange.IntRange.ANY, new ComponentsPredicate(predicate, Collections.emptyMap())));
        }
    }

    @Override
    public String getCustomName() {
        T container = this.getSnapshot();
        return container.customName != null ? CraftChatMessage.fromComponent(container.getCustomName()) : null;
    }

    @Override
    public void setCustomName(String name) {
        // this.getSnapshot().setCustomName(CraftChatMessage.fromStringOrNull(name));
    	this.getSnapshot().customName = (CraftChatMessage.fromStringOrNull(name));
    }

    @Override
    public void applyTo(T container) {
        super.applyTo(container);
        if (this.getSnapshot().customName == null) container.customName = null;// container.setCustomName(null);
    }
    
    @Override
    public void setLockItem(ItemStack key) {
        if (key == null) {
            this.getSnapshot().lock = ContainerLock.EMPTY;
        } else {
            this.getSnapshot().lock = new ContainerLock(CraftItemStack.asCriterionConditionItem(key));
        }
    }

}
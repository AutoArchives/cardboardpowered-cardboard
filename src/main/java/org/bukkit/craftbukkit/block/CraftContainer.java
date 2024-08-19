package org.bukkit.craftbukkit.block;

import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.inventory.ContainerLock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.craftbukkit.util.CraftChatMessage;
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
        return !this.getSnapshot().lock.key.isEmpty();
    }

    @Override
    public String getLock() {
        return this.getSnapshot().lock.key;
    }

    @Override
    public void setLock(String key) {
        this.getSnapshot().lock = (key == null) ? ContainerLock.EMPTY : new ContainerLock(key);
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

}
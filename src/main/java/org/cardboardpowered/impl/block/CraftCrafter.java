package org.cardboardpowered.impl.block;

import com.google.common.base.Preconditions;

import net.kyori.adventure.text.Component;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.inventory.Inventory;
import org.bukkit.Location;
import org.bukkit.World;
// TODO import org.bukkit.block.Crafter;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.jetbrains.annotations.Nullable;

public class CraftCrafter extends CardboardLootableBlock<CrafterBlockEntity>
// implements Crafter
{

    public CraftCrafter(World world, CrafterBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CraftCrafter(CraftCrafter state, Location location) {
        super(state, location);
    }

    public org.bukkit.inventory.Inventory getSnapshotInventory() {
        return new CraftInventory((Inventory)this.getSnapshot());
    }

    public org.bukkit.inventory.Inventory getInventory() {
        if (!this.isPlaced()) {
            return this.getSnapshotInventory();
        }
        return new CraftInventory((Inventory)this.getTileEntity());
    }

    @Override
    public CraftCrafter copy() {
        return new CraftCrafter(this, null);
    }

    @Override
    public CraftCrafter copy(Location location) {
        return new CraftCrafter(this, location);
    }

    public int getCraftingTicks() {
        return 0;
    	// TODO return ((CrafterBlockEntity)this.getSnapshot()).craftingTicksRemaining;
    }

    public void setCraftingTicks(int ticks) {
        ((CrafterBlockEntity)this.getSnapshot()).setCraftingTicksRemaining(ticks);
    }

    public boolean isSlotDisabled(int slot) {
        Preconditions.checkArgument((slot >= 0 && slot < 9 ? 1 : 0) != 0, (String)"Invalid slot index %s for Crafter", (int)slot);
        return ((CrafterBlockEntity)this.getSnapshot()).isSlotDisabled(slot);
    }

    public void setSlotDisabled(int slot, boolean disabled) {
        Preconditions.checkArgument((slot >= 0 && slot < 9 ? 1 : 0) != 0, (String)"Invalid slot index %s for Crafter", (int)slot);
        ((CrafterBlockEntity)this.getSnapshot()).setSlotEnabled(slot, disabled);
    }

    public boolean isTriggered() {
        return ((CrafterBlockEntity)this.getSnapshot()).isTriggered();
    }

    public void setTriggered(boolean triggered) {
        ((CrafterBlockEntity)this.getSnapshot()).setTriggered(triggered);
    }

	@Override
	public @Nullable Component customName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void customName(@Nullable Component arg0) {
		// TODO Auto-generated method stub
		
	}

}
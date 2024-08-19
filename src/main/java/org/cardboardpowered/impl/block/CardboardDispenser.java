package org.cardboardpowered.impl.block;

import net.kyori.adventure.text.Component;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.DispenserBlockEntity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.projectiles.BlockProjectileSource;
import org.cardboardpowered.impl.world.WorldImpl;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CardboardDispenser extends CardboardLootableBlock<DispenserBlockEntity> implements Dispenser {

    public CardboardDispenser(World world, DispenserBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardDispenser(CardboardDispenser state, Location location) {
        super(state, location);
    }
    
    @Override
    public CardboardDispenser copy() {
        return new CardboardDispenser(this, null);
    }

    @Override
    public CardboardDispenser copy(Location location) {
        return new CardboardDispenser(this, location);
    }

	@Override
	public Inventory getSnapshotInventory() {
		return new CraftInventory(this.getSnapshot());
	}

	@Override
	public Inventory getInventory() {
		return (!this.isPlaced()) ? this.getSnapshotInventory() : new CraftInventory(this.getTileEntity());
	}

	@Override
	public BlockProjectileSource getBlockProjectileSource() {
		if(getBlock().getType() != Material.DISPENSER) return null;
		return new CardboardBlockProjectileSource((DispenserBlockEntity) this.getTileEntityFromWorld());
	}

	@Override
	public boolean dispense() {
		Block block = getBlock();
		if(block.getType() != Material.DISPENSER) return false;

		BlockState state = world.getHandle().getBlockState(getPosition());
		((DispenserBlock) Blocks.DISPENSER).dispense(((WorldImpl) this.getWorld()).getHandle(), state, this.getPosition());
		return true;
	}

	@Override
	public long getLastFilled() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Long getLastLooted(UUID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getNextRefill() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasBeenFilled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasPendingRefill() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasPlayerLooted(UUID arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRefillEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setHasPlayerLooted(UUID arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long setNextRefill(long arg0) {
		// TODO Auto-generated method stub
		return 0;
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

package org.cardboardpowered.impl.block;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import net.kyori.adventure.text.Component;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.text.Text;

public class CardboardHopper extends CardboardLootableBlock<HopperBlockEntity> implements Hopper {

    public CardboardHopper(World world, HopperBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardHopper(CardboardHopper state, Location location) {
        super(state, location);
    }
	
    @Override
    public CardboardHopper copy() {
        return new CardboardHopper(this, null);
    }

    @Override
    public CardboardHopper copy(Location location) {
        return new CardboardHopper(this, location);
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
        return CardboardAdventure.asAdventure(Text.of(this.getCustomName()));
    }

    @Override
    public void customName(@Nullable Component arg0) {
        // TODO Auto-generated method stub
    	this.setCustomName( CardboardAdventure.asVanilla(arg0).getLiteralString() );
    }
    
    // 1.20.4 API:

	@Override
	public void setTransferCooldown(int cooldown) {
		// ((HopperBlockEntity)this.getSnapshot()).setTransferCooldown(cooldown);
	}

	@Override
	public int getTransferCooldown() {
		return 0; // ((HopperBlockEntity)this.getSnapshot()).transferCooldown;
	}

}
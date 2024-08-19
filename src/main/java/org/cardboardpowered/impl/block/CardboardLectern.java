package org.cardboardpowered.impl.block;

import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.LecternBlockEntity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Lectern;
import org.bukkit.inventory.Inventory;
import org.cardboardpowered.impl.inventory.CardboardLecternInventory;

public class CardboardLectern extends CardboardBlockEntityState<LecternBlockEntity> implements Lectern {

    public CardboardLectern(World world, LecternBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardLectern(CardboardLectern state, Location location) {
        super(state, location);
    }
    
    @Override
    public CardboardLectern copy() {
        return new CardboardLectern(this, null);
    }

    @Override
    public CardboardLectern copy(Location location) {
        return new CardboardLectern(this, location);
    }

    @Override
    public int getPage() {
        return getSnapshot().getCurrentPage();
    }

    @Override
    public void setPage(int page) {
        getSnapshot().setCurrentPage(page);
    }

    @Override
    public Inventory getSnapshotInventory() {
        return new CardboardLecternInventory(this.getSnapshot().inventory);
    }

    @Override
    public Inventory getInventory() {
        return (!this.isPlaced()) ? this.getSnapshotInventory() : new CardboardLecternInventory(this.getTileEntity().inventory);
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result && this.isPlaced() && this.getType() == Material.LECTERN)
            LecternBlock.setPowered(this.world.getHandle(), this.getPosition(), this.getHandle());
        return result;
    }

}
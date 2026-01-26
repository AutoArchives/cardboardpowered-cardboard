package org.cardboardpowered.impl.block;

import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Lectern;
import org.bukkit.inventory.Inventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryLectern;

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
        return getSnapshot().getPage();
    }

    @Override
    public void setPage(int page) {
        getSnapshot().setPage(page);
    }

    @Override
    public Inventory getSnapshotInventory() {
        return new CraftInventoryLectern(this.getSnapshot().bookAccess);
    }

    @Override
    public Inventory getInventory() {
        return (!this.isPlaced()) ? this.getSnapshotInventory() : new CraftInventoryLectern(this.getTileEntity().bookAccess);
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result && this.isPlaced() && this.getType() == Material.LECTERN)
            LecternBlock.signalPageChange(this.world.getHandle(), this.getPosition(), this.getHandle());
        return result;
    }

}
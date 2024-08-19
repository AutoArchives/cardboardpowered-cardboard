package org.cardboardpowered.impl.block;

import net.minecraft.block.entity.BrushableBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

public class CraftBrushableBlock extends CardboardBlockEntityState<BrushableBlockEntity>
// implements BrushableBlock 
{

    public CraftBrushableBlock(World world, BrushableBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CraftBrushableBlock(CraftBrushableBlock state, Location location) {
        super(state, location);
    }

    public ItemStack getItem() {
        return CraftItemStack.asBukkitCopy(((BrushableBlockEntity)this.getSnapshot()).getItem());
    }

    public void setItem(ItemStack item) {
        // TODO
    	// ((BrushableBlockEntity)this.getSnapshot()).item = CraftItemStack.asNMSCopy(item);
    }

    @Override
    public void applyTo(BrushableBlockEntity lootable) {
        super.applyTo(lootable);
        // TODO
        //if (((BrushableBlockEntity)this.getSnapshot()).lootTable == null) {
        //    lootable.setLootTable(null, 0L);
        //}
    }

    public LootTable getLootTable() {
    	return null;
        // TODO return CraftLootTable.minecraftToBukkit(((BrushableBlockEntity)this.getSnapshot()).lootTable);
    }

    public void setLootTable(LootTable table) {
        this.setLootTable(table, this.getSeed());
    }

    public long getSeed() {
        return -1;
    	// TODO return ((BrushableBlockEntity)this.getSnapshot()).lootTableSeed;
    }

    public void setSeed(long seed) {
        this.setLootTable(this.getLootTable(), seed);
    }

    public void setLootTable(LootTable table, long seed) {
        // TODO
    	// ((BrushableBlockEntity)this.getSnapshot()).setLootTable(CraftLootTable.bukkitToMinecraft(table), seed);
    }

    @Override
    public CraftBrushableBlock copy() {
        return new CraftBrushableBlock(this, null);
    }

    @Override
    public CraftBrushableBlock copy(Location location) {
        return new CraftBrushableBlock(this, location);
    }

}
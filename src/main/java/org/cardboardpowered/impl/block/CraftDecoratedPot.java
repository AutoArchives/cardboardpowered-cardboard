package org.cardboardpowered.impl.block;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.DecoratedPot;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftDecoratedPot extends CardboardBlockEntityState<DecoratedPotBlockEntity> implements DecoratedPot {

    public CraftDecoratedPot(World world, DecoratedPotBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CraftDecoratedPot(CraftDecoratedPot state, Location location) {
        super(state, location);
    }

    /*
    public DecoratedPotInventory getSnapshotInventory() {
        return new CraftInventoryDecoratedPot((Inventory)this.getSnapshot());
    }

    public DecoratedPotInventory getInventory() {
        if (!this.isPlaced()) {
            return this.getSnapshotInventory();
        }
        return new CraftInventoryDecoratedPot((Inventory)this.getTileEntity());
    }
    */

    public void setLootTable(LootTable table) {
        this.setLootTable(table, this.getSeed());
    }

    public void setLootTable(LootTable table, long seed) {
        RegistryKey<net.minecraft.loot.LootTable> key = table == null ? null : RegistryKey.of(RegistryKeys.LOOT_TABLE, CraftNamespacedKey.toMinecraft(table.getKey()));
        ((DecoratedPotBlockEntity)this.getSnapshot()).setLootTable(key, seed);
    }

    public LootTable getLootTable() {
        if (((DecoratedPotBlockEntity)this.getSnapshot()).getLootTable() == null) {
            return null;
        }
        RegistryKey<net.minecraft.loot.LootTable> key = ((DecoratedPotBlockEntity)this.getSnapshot()).getLootTable();
        return Bukkit.getLootTable((NamespacedKey)CraftNamespacedKey.fromMinecraft(key.getValue()));
    }

    public void setSeed(long seed) {
        ((DecoratedPotBlockEntity)this.getSnapshot()).setLootTableSeed(seed);
    }

    public long getSeed() {
        return ((DecoratedPotBlockEntity)this.getSnapshot()).getLootTableSeed();
    }

    public List<Material> getShards() {
        return ((DecoratedPotBlockEntity)this.getSnapshot()).getSherds().stream().stream().map(CraftItemType::minecraftToBukkit).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public CraftDecoratedPot copy() {
        return new CraftDecoratedPot(this, null);
    }

    @Override
    public CraftDecoratedPot copy(Location location) {
        return new CraftDecoratedPot(this, location);
    }

	// @Override
	public void addShard(@NotNull Material arg0) {
		// TODO Auto-generated method stub
		
	}

	// @Override
	public void setShards(@Nullable List<Material> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSherd(@NotNull Side side, @Nullable Material sherd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public @NotNull Material getSherd(@NotNull Side side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull Map<Side, Material> getSherds() {
		// TODO Auto-generated method stub
		return null;
	}

}
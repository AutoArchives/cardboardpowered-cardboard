package org.cardboardpowered.impl.block;

import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftContainer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

import me.isaiah.common.cmixin.IMixinLootableContainerBlockEntity;

public abstract class CardboardLootableBlock<T extends LootableContainerBlockEntity> extends CraftContainer<T> implements Nameable, Lootable {

	
    public CardboardLootableBlock(World world, T tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardLootableBlock(CardboardLootableBlock<T> state, Location location) {
        super(state, location);
    }
	
    /*
    public CardboardLootableBlock(Block block, Class<T> tileEntityClass) {
        super(block, tileEntityClass);
    }

    public CardboardLootableBlock(Material material, T tileEntity) {
        super(material, tileEntity);
    }*/

    public Identifier get_table_id() {
    	IMixinLootableContainerBlockEntity be = (IMixinLootableContainerBlockEntity) (LootableContainerBlockEntity) this.getSnapshot();
    	if (null == be.IC$get_loot_table_id()) {
    		return null;
    	}
    	return be.IC$get_loot_table_id();
    }
    
    @Override
    public void applyTo(T lootable) {
        super.applyTo(lootable);
        
        IMixinLootableContainerBlockEntity be = (IMixinLootableContainerBlockEntity) (LootableContainerBlockEntity) this.getSnapshot();
        
        if (null == be.IC$get_loot_table_id()) { 
        	lootable.setLootTable(null, 0L);
        }
    }

    @Override
    public LootTable getLootTable() {
        return (get_table_id() == null) ? null : Bukkit.getLootTable(CraftNamespacedKey.fromMinecraft(get_table_id()));
    }

    @Override
    public void setLootTable(LootTable table) {
        setLootTable(table, getSeed());
    }

    @Override
    public long getSeed() {
        return getSnapshot().lootTableSeed;
    }

    @Override
    public void setSeed(long seed) {
        setLootTable(getLootTable(), seed);
    }

    public void setLootTable(LootTable table, long seed) {

    	// RegistryKeys.LOOT_TABLE
    	
    	Registry<net.minecraft.loot.LootTable> reg = CraftServer.server.getRegistryManager().get(RegistryKeys.LOOT_TABLE);
    	Optional<net.minecraft.loot.LootTable> mc_table = reg.getOrEmpty( CraftNamespacedKey.toMinecraft(table.getKey()) );
    	
    	if (mc_table.isPresent()) {
    		Optional<RegistryKey<net.minecraft.loot.LootTable>> mc_key = reg.getKey(mc_table.get());
    		getSnapshot().setLootTable(mc_key.get(), seed);
    	} else {
    		getSnapshot().setLootTable(null, seed);
    	}
    	
        // getSnapshot().setLootTable(((table == null) ? null : CraftNamespacedKey.toMinecraft(table.getKey())), seed);
    }
    
    @Override
    public abstract CardboardLootableBlock<T> copy();

    @Override
    public abstract CardboardLootableBlock<T> copy(Location var1);

}
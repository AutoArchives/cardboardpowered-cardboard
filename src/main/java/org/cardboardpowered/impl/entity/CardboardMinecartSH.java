package org.cardboardpowered.impl.entity;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public abstract class CardboardMinecartSH extends CardboardMinecart implements Lootable {

    public CardboardMinecartSH(CraftServer server, AbstractMinecartEntity entity) {
        super(server, entity);
    }

    @Override
    public StorageMinecartEntity getHandle() {
        return (StorageMinecartEntity) nms;
    }

    @Override
    public void setLootTable(LootTable table) {
        setLootTable(table, getSeed());
    }

    @Override
    public LootTable getLootTable() {
        // Identifier nmsTable = getHandle().lootTableId;
        
    	Identifier nmsTable = getHandle().getLootTable().getValue();

        return (nmsTable == null) ? null : Bukkit.getLootTable(CraftNamespacedKey.fromMinecraft(nmsTable));
    }

    @Override
    public void setSeed(long seed) {
        setLootTable(getLootTable(), seed);
    }

    @Override
    public long getSeed() {
        return -1; // TODO lootSeed
    }

    public void setLootTable(LootTable table, long seed) {
        Identifier newKey = (table == null) ? null : CraftNamespacedKey.toMinecraft(table.getKey());
        // getHandle().setLootTable(newKey, seed);
        
    	Registry<net.minecraft.loot.LootTable> reg = CraftServer.server.getRegistryManager().get(RegistryKeys.LOOT_TABLE);
    	Optional<net.minecraft.loot.LootTable> mc_table = reg.getOrEmpty( newKey );
    	
    	if (mc_table.isPresent()) {
    		Optional<RegistryKey<net.minecraft.loot.LootTable>> mc_key = reg.getKey(mc_table.get());
    		getHandle().setLootTable(mc_key.get(), seed);
    	} else {
    		getHandle().setLootTable(null, seed);
    	}
    }
}

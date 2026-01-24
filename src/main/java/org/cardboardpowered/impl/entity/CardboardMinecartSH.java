package org.cardboardpowered.impl.entity;

import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

public abstract class CardboardMinecartSH extends CardboardMinecart implements Lootable {

    public CardboardMinecartSH(CraftServer server, AbstractMinecart entity) {
        super(server, entity);
    }

    @Override
    public AbstractMinecartContainer getHandle() {
        return (AbstractMinecartContainer) nms;
    }

    @Override
    public void setLootTable(LootTable table) {
        setLootTable(table, getSeed());
    }

    @Override
    public LootTable getLootTable() {
        // Identifier nmsTable = getHandle().lootTableId;
        
    	Identifier nmsTable = getHandle().getContainerLootTable().identifier();

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
        
    	Registry<net.minecraft.world.level.storage.loot.LootTable> reg = CraftServer.server.registryAccess().lookupOrThrow(Registries.LOOT_TABLE);
    	Optional<net.minecraft.world.level.storage.loot.LootTable> mc_table = reg.getOptional( newKey );
    	
    	if (mc_table.isPresent()) {
    		Optional<ResourceKey<net.minecraft.world.level.storage.loot.LootTable>> mc_key = reg.getResourceKey(mc_table.get());
    		getHandle().setLootTable(mc_key.get(), seed);
    	} else {
    		getHandle().setLootTable(null, seed);
    	}
    }
}

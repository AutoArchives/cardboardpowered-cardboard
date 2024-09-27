package com.destroystokyo.paper.loottable;

import com.destroystokyo.paper.loottable.LootableBlockInventory;
import com.destroystokyo.paper.loottable.LootableInventory;
import com.destroystokyo.paper.loottable.PaperLootableInventory;
import com.destroystokyo.paper.loottable.PaperLootableInventoryData;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;

public interface PaperLootableBlockInventory
extends LootableBlockInventory,
PaperLootableInventory {
    public LootableContainerBlockEntity getTileEntity();

    @Override
    default public LootableInventory getAPILootableInventory() {
        return this;
    }

    @Override
    default public World getNMSWorld() {
        return this.getTileEntity().getWorld();
    }

    default public Block getBlock() {
        BlockPos position = this.getTileEntity().getPos();
        Chunk bukkitChunk = this.getBukkitWorld().getChunkAt((Block)CraftBlock.at((ServerWorld) this.getNMSWorld(), position));
        return bukkitChunk.getBlock(position.getX(), position.getY(), position.getZ());
    }

    @Override
    default public PaperLootableInventoryData getLootableData() {
        return null; // this.getTileEntity().lootableData;
    }
}


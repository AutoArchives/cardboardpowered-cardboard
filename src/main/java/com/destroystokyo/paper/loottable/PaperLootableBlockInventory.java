package com.destroystokyo.paper.loottable;

import com.destroystokyo.paper.loottable.LootableBlockInventory;
import com.destroystokyo.paper.loottable.LootableInventory;
import com.destroystokyo.paper.loottable.PaperLootableInventory;
import com.destroystokyo.paper.loottable.PaperLootableInventoryData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;

public interface PaperLootableBlockInventory
extends LootableBlockInventory,
PaperLootableInventory {
    public RandomizableContainerBlockEntity getTileEntity();

    @Override
    default public LootableInventory getAPILootableInventory() {
        return this;
    }

    @Override
    default public Level getNMSWorld() {
        return this.getTileEntity().getLevel();
    }

    default public Block getBlock() {
        BlockPos position = this.getTileEntity().getBlockPos();
        Chunk bukkitChunk = this.getBukkitWorld().getChunkAt((Block)CraftBlock.at((ServerLevel) this.getNMSWorld(), position));
        return bukkitChunk.getBlock(position.getX(), position.getY(), position.getZ());
    }

    @Override
    default public PaperLootableInventoryData getLootableData() {
        return null; // this.getTileEntity().lootableData;
    }
}


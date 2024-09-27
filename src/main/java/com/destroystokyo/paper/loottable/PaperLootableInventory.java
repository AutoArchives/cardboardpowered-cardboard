package com.destroystokyo.paper.loottable;

import com.javazilla.bukkitfabric.interfaces.IMixinWorld;

import java.util.UUID;
import net.minecraft.world.World;
import org.bukkit.loot.Lootable;

public interface PaperLootableInventory
extends LootableInventory,
Lootable {
    public PaperLootableInventoryData getLootableData();

    public LootableInventory getAPILootableInventory();

    public World getNMSWorld();

    default public org.bukkit.World getBukkitWorld() {
        return ((IMixinWorld)this.getNMSWorld()).getWorldImpl();
    }

    default public boolean isRefillEnabled() {
        return true; // this.getNMSWorld().paperConfig().lootables.autoReplenish;
    }

    default public boolean hasBeenFilled() {
        return this.getLastFilled() != -1L;
    }

    default public boolean hasPlayerLooted(UUID player) {
        return this.getLootableData().hasPlayerLooted(player);
    }

    default public boolean canPlayerLoot(UUID player) {
        return this.getLootableData().canPlayerLoot(player, null);
    }

    default public Long getLastLooted(UUID player) {
        return this.getLootableData().getLastLooted(player);
    }

    default public boolean setHasPlayerLooted(UUID player, boolean looted) {
        boolean hasLooted = this.hasPlayerLooted(player);
        if (hasLooted != looted) {
            this.getLootableData().setPlayerLootedState(player, looted);
        }
        return hasLooted;
    }

    default public boolean hasPendingRefill() {
        long nextRefill = this.getLootableData().getNextRefill();
        return nextRefill != -1L && nextRefill > this.getLootableData().getLastFill();
    }

    default public long getLastFilled() {
        return this.getLootableData().getLastFill();
    }

    default public long getNextRefill() {
        return this.getLootableData().getNextRefill();
    }

    default public long setNextRefill(long refillAt) {
        if (refillAt < -1L) {
            refillAt = -1L;
        }
        return this.getLootableData().setNextRefill(refillAt);
    }
}


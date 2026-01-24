package org.cardboardpowered.impl.inventory;

import net.minecraft.world.Container;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.inventory.BeaconInventory;
import org.bukkit.inventory.ItemStack;

public class CardboardBeaconInventory extends CraftInventory implements BeaconInventory {

    public CardboardBeaconInventory(Container beacon) {
        super(beacon);
    }

    @Override
    public void setItem(ItemStack item) {
        setItem(0, item);
    }

    @Override
    public ItemStack getItem() {
        return getItem(0);
    }

}
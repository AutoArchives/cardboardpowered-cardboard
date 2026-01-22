package org.cardboardpowered.impl.inventory;

import net.minecraft.world.Container;
import org.bukkit.craftbukkit.inventory.CraftResultInventory;
import org.bukkit.inventory.StonecutterInventory;

public class CardboardStonecutterInventory extends CraftResultInventory implements StonecutterInventory {

    public CardboardStonecutterInventory(Container inventory, Container resultInventory) {
        super(inventory, resultInventory);
    }

}
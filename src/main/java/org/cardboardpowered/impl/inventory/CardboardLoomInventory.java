package org.cardboardpowered.impl.inventory;

import net.minecraft.world.Container;
import org.bukkit.craftbukkit.inventory.CraftResultInventory;
import org.bukkit.inventory.LoomInventory;

public class CardboardLoomInventory extends CraftResultInventory implements LoomInventory {

    public CardboardLoomInventory(Container inventory, Container resultInventory) {
        super(inventory, resultInventory);
    }

}
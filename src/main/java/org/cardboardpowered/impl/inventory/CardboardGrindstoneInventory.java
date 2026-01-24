package org.cardboardpowered.impl.inventory;

import net.minecraft.world.Container;
import org.bukkit.craftbukkit.inventory.CraftResultInventory;
import org.bukkit.inventory.GrindstoneInventory;

public class CardboardGrindstoneInventory extends CraftResultInventory implements GrindstoneInventory {

    public CardboardGrindstoneInventory(Container inventory, Container resultInventory) {
        super(inventory, resultInventory);
    }

}
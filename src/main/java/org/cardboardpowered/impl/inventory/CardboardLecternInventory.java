package org.cardboardpowered.impl.inventory;

import net.minecraft.world.Container;
import org.bukkit.block.Lectern;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.inventory.LecternInventory;

import org.cardboardpowered.interfaces.IMixinInventory;

public class CardboardLecternInventory extends CraftInventory implements LecternInventory {

    public CardboardLecternInventory(Container inventory) {
        super(inventory);
    }

    @Override
    public Lectern getHolder() {
        return (Lectern) ((IMixinInventory)(Object)inventory).getOwner();
    }

}
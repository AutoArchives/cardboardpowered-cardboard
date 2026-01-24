package org.cardboardpowered.impl.inventory;

import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

public class CustomInventoryView extends CardboardInventoryView {

    public CustomInventoryView(HumanEntity player, Inventory viewing, AbstractContainerMenu container) {
        super(player, viewing, container);
    }

}
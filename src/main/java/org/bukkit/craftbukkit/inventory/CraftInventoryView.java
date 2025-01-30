package org.bukkit.craftbukkit.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;

import net.minecraft.screen.ScreenHandler;

public class CraftInventoryView<T extends ScreenHandler, I extends Inventory> extends CardboardInventoryView<T, I> {

	public CraftInventoryView(HumanEntity player, I viewing, ScreenHandler container) {
		super(player, viewing, container);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public I getTopInventory() {
        return this.viewing;
    }

}

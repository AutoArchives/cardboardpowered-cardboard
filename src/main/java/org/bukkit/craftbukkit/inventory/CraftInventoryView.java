package org.bukkit.craftbukkit.inventory;

import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;

public class CraftInventoryView<T extends AbstractContainerMenu, I extends Inventory> extends CardboardInventoryView<T, I> {

	public CraftInventoryView(HumanEntity player, I viewing, AbstractContainerMenu container) {
		super(player, viewing, container);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public I getTopInventory() {
        return this.viewing;
    }

}

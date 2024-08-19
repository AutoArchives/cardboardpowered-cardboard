package org.cardboardpowered.interfaces;

import org.bukkit.inventory.ItemStack;

import net.minecraft.component.ComponentChanges;
import net.minecraft.item.Item;

public interface IItemStack {

	void cardboard$restore_patch(ComponentChanges changes);

	public ItemStack getBukkitStack();

	void cb$setItem(Item item);

}

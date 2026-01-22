package org.cardboardpowered.interfaces;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IItemStack {

	void cardboard$restore_patch(DataComponentPatch changes);

	public ItemStack getBukkitStack();

	void cb$setItem(Item item);

	ItemStack asBukkitCopy();

}

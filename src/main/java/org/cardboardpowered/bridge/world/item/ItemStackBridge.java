package org.cardboardpowered.bridge.world.item;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import org.bukkit.inventory.ItemStack;

public interface ItemStackBridge {

	void cardboard$restore_patch(DataComponentPatch changes);

	public ItemStack getBukkitStack();

	void cb$setItem(Item item);

	ItemStack asBukkitCopy();

}

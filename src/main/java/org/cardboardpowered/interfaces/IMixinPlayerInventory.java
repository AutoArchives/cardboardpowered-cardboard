package org.cardboardpowered.interfaces;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface IMixinPlayerInventory extends IMixinInventory {

    int canHold(ItemStack itemstack);

	List<ItemStack> getArmorContents();

	List<ItemStack> getExtraContent();

}
package org.cardboardpowered.interfaces;

import org.cardboardpowered.interfaces.IMixinInventory;

import net.minecraft.item.ItemStack;

public interface IMixinPlayerInventory extends IMixinInventory {

    int canHold(ItemStack itemstack);

}
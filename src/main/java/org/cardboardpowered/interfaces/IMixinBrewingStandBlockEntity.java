package org.cardboardpowered.interfaces;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public interface IMixinBrewingStandBlockEntity {

    public NonNullList<ItemStack> cardboard_getInventory();

}
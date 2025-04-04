package org.cardboardpowered.interfaces;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IMixinThrownItemEntity {

    Item getDefaultItemPublic();

    @Deprecated
    ItemStack getItemBF();

}

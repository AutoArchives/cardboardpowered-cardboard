package org.cardboardpowered.interfaces;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IMixinThrownItemEntity {

    Item getDefaultItemPublic();

    @Deprecated
    ItemStack getItemBF();

}

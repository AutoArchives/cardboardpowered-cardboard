/**
 * Cardboard - Paper API for Fabric
 * Copyright (C) 2020-2025
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 3 of the License, or (at your option) any later version.
 */
package org.cardboardpowered.interfaces;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.inventory.InventoryHolder;

public interface IMixinInventory {

    java.util.List<ItemStack> getContents();

    void onOpen(CraftHumanEntity who);

    void onClose(CraftHumanEntity who);

    java.util.List<org.bukkit.entity.HumanEntity> getViewers();

  
    org.bukkit.inventory.InventoryHolder getOwner();

    void setCardboardMaxStackSize(int size);

    org.bukkit.Location getLocation();

    default Recipe<?> getCurrentRecipe() {
        return null;
    }

    default void setCurrentRecipe(Recipe<?> recipe) {
    }

    int MAX_STACK = 64;

    int getCardboardMaxStackSize();

    default void cardboard$setOwner(InventoryHolder owner) {
        // TODO
    }

}
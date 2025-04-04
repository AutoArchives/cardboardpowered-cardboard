/**
 * Cardboard - CardboardPowered.org
 * Copyright (C) 2020 CardboardPowered.org and contributors
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.cardboardpowered.interfaces;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.cardboardpowered.interfaces.IScreenHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

public interface IMixinScreenHandler extends IScreenHandler {

    CardboardInventoryView getBukkitView();

    Text getTitle();

    void setTitle(Text title);

    void transferTo(ScreenHandler other, CraftHumanEntity player);
    
    /*
    public default void transferTo(ScreenHandler other, CraftHumanEntity player) {
    	CardboardInventoryView source = this.getBukkitView();
    	CardboardInventoryView destination = other.getBukkitView();

        ( (IMixinInventory) ((CraftInventory)source.getTopInventory()).getInventory() ).onClose(player);
        ( (IMixinInventory) ((CraftInventory)source.getBottomInventory()).getInventory() ).onClose(player);
        ( (IMixinInventory) ((CraftInventory)destination.getTopInventory()).getInventory() ).onOpen(player);
        ( (IMixinInventory) ((CraftInventory)destination.getBottomInventory()).getInventory() ).onOpen(player);
    }
    */


    DefaultedList<ItemStack> getTrackedStacksBF();

    void setTrackedStacksBF(DefaultedList<ItemStack> trackedStacks);

    void setCheckReachable(boolean bl);

    void cardboard_setSlots(DefaultedList<Slot> slots);

    DefaultedList<ItemStack> cardboard_previousTrackedStacks();

    void cardboard_previousTrackedStacks(DefaultedList<ItemStack> s);

}
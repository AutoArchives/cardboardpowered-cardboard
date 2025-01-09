/**
 * Cardboard - Bukkit for Fabric Project
 * Copyright (C) 2020-2025 Cardboard contributors
 */
package org.cardboardpowered.interfaces;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntryList;

public interface IIngredient {

    boolean getExact_BF();

    void setExact_BF(boolean value);

	void cardboard$set_itemStacks(List<ItemStack> stacks);

	static Ingredient cb$ofStacks(List<ItemStack> stacks) {
		Ingredient recipe = Ingredient.ofItems(stacks.stream().map(ItemStack::getItem));
		((IIngredient)recipe).cardboard$set_itemStacks(stacks);
		return recipe;
	}

	boolean cb$isExact();

	RegistryEntryList<Item> cb$entries();

	List<ItemStack> cb$itemStacks();

}
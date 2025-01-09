/**
 */
package com.javazilla.bukkitfabric.interfaces;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.cardboardpowered.impl.inventory.recipe.RecipeInterface;

import com.google.common.collect.Multimap;

import java.util.Map;

public interface IMixinRecipeManager {

    default void addRecipe(NamespacedKey key, Recipe<?> recipe) {
        addRecipe(new RecipeEntry<>(
        		RecipeInterface.toMinecraft(key),
                recipe
        ));
    }

    void addRecipe(RecipeEntry<?> recipeEntry);

    // Map<RecipeType<?>, Map<Identifier, RecipeEntry<?>>> getRecipes();

    void clearRecipes();

	boolean removeRecipe(Identifier mcKey);

	Multimap<RecipeType<?>, RecipeEntry<?>> cb$get_recipesByType();

}

/**
 */
package org.cardboardpowered.interfaces;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftRecipe;

import com.google.common.collect.Multimap;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

public interface IMixinRecipeManager {

    default void addRecipe(NamespacedKey key, Recipe<?> recipe) {
        addRecipe(new RecipeHolder<>(
        		CraftRecipe.toMinecraft(key),
                recipe
        ));
    }

    void addRecipe(RecipeHolder<?> recipeEntry);

    // Map<RecipeType<?>, Map<Identifier, RecipeEntry<?>>> getRecipes();

    void clearRecipes();

	boolean removeRecipe(Identifier mcKey);

	Multimap<RecipeType<?>, RecipeHolder<?>> cb$get_recipesByType();

}

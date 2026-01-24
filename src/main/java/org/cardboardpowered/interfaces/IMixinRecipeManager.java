/**
 */
package org.cardboardpowered.interfaces;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.cardboardpowered.impl.inventory.recipe.RecipeInterface;

import com.google.common.collect.Multimap;

import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

public interface IMixinRecipeManager {

    default void addRecipe(NamespacedKey key, Recipe<?> recipe) {
        addRecipe(new RecipeHolder<>(
        		RecipeInterface.toMinecraft(key),
                recipe
        ));
    }

    void addRecipe(RecipeHolder<?> recipeEntry);

    // Map<RecipeType<?>, Map<Identifier, RecipeEntry<?>>> getRecipes();

    void clearRecipes();

	boolean removeRecipe(Identifier mcKey);

	Multimap<RecipeType<?>, RecipeHolder<?>> cb$get_recipesByType();

}

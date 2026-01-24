package org.cardboardpowered.impl.inventory.recipe;

import org.cardboardpowered.interfaces.IMixinMinecraftServer;
import org.cardboardpowered.interfaces.IMixinRecipe;
import org.cardboardpowered.interfaces.IMixinRecipeManager;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

public class RecipeIterator implements Iterator<Recipe> {

    // private final Iterator<Entry<RecipeType<?>, Map<Identifier, RecipeEntry<?>>>> recipes;
    // private Iterator<RecipeEntry<?>> current;
    
    private final Iterator<Map.Entry<RecipeType<?>, RecipeHolder<?>>> recipes;

    public RecipeIterator() {
    	
    	this.recipes = ((IMixinRecipeManager)IMixinMinecraftServer.getServer().getRecipeManager()).cb$get_recipesByType().entries().iterator();
    	
        // this.recipes = ((IMixinRecipeManager)IMixinMinecraftServer.getServer().getRecipeManager()).getRecipes().entrySet().iterator();
    }

    @Override
    public boolean hasNext() {
    	return this.recipes.hasNext();
    }

    @Override
    public Recipe next() {
        // if (current == null || !current.hasNext()) current = recipes.next().getValue().values().iterator();
        // return ((IMixinRecipe)(Object) current.next()).toBukkitRecipe();
        
        return ( (IMixinRecipe) (Object) this.recipes.next().getValue() ) .toBukkitRecipe();
    }

    @Override
    public void remove() {
        // if (current == null) throw new IllegalStateException("next() not yet called");
    	this.recipes.remove();
    }

}

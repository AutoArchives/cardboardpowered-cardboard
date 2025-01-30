package org.bukkit.craftbukkit.inventory.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.screen.StonecutterScreenHandler;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.StonecutterInventory;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.view.StonecutterView;
import org.jetbrains.annotations.NotNull;

import com.javazilla.bukkitfabric.interfaces.IMixinRecipe;

public class CraftStonecutterView extends CraftInventoryView<StonecutterScreenHandler, StonecutterInventory> implements StonecutterView {

    public CraftStonecutterView(final HumanEntity player, final StonecutterInventory viewing, final StonecutterScreenHandler container) {
        super(player, viewing, container);
    }

    @Override
    public int getSelectedRecipeIndex() {
        return ((StonecutterScreenHandler)this.container).getSelectedRecipe();
    }
    
    // @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        /*
    	CraftItemStack result = CraftItemStack.asCraftMirror(this.result());
        CraftStonecuttingRecipe recipe = new CraftStonecuttingRecipe(id, result, CraftRecipe.toBukkit(this.ingredient()));
        recipe.setGroup(this.getGroup());
        return recipe;
        */
    	return null;
    }

    @NotNull
    @Override
    public List<StonecuttingRecipe> getRecipes() {
        final List<StonecuttingRecipe> recipes = new ArrayList<>();
        for (final CuttingRecipeDisplay.GroupEntry<net.minecraft.recipe.StonecuttingRecipe> recipe : ((StonecutterScreenHandler)this.container).getAvailableRecipes().entries()) {
            
        	Optional<RecipeEntry<net.minecraft.recipe.StonecuttingRecipe>> opt = recipe.recipe().recipe();
        	
        	if (opt.isPresent()) {
        		RecipeEntry<net.minecraft.recipe.StonecuttingRecipe> rep = opt.get();

        		Recipe bukkit = ((IMixinRecipe) (Object) rep).toBukkitRecipe();
        		recipes.add((StonecuttingRecipe) bukkit);
        	}
        	
        	// recipe.recipe().recipe().map(RecipeEntry::toBukkitRecipe).ifPresent((bukkit) -> recipes.add((StonecuttingRecipe) bukkit));
        }
        return recipes;
    }

    @Override
    public int getRecipeAmount() {
        return ((StonecutterScreenHandler)this.container).getAvailableRecipeCount();
    }
}

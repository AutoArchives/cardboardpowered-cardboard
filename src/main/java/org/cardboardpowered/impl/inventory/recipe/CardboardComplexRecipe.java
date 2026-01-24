package org.cardboardpowered.impl.inventory.recipe;

import org.cardboardpowered.interfaces.IMixinMinecraftServer;
import org.cardboardpowered.interfaces.IMixinRecipeManager;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ComplexRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.CraftingRecipe;

public class CardboardComplexRecipe extends CraftingRecipe implements RecipeInterface, ComplexRecipe {

    // private final RecipeEntry<SpecialCraftingRecipe> recipe;

    //public CardboardComplexRecipe(RecipeEntry<SpecialCraftingRecipe> recipe) {
    //    this.recipe = recipe;
    //}
    
    private final CustomRecipe recipe;

    public CardboardComplexRecipe(NamespacedKey key, ItemStack result, CustomRecipe recipe) {
        super(key, result);
        this.recipe = recipe;
    }

    /*
    @Override
    public ItemStack getResult() {
        return CraftItemStack.asCraftMirror(recipe.value().getResult(DynamicRegistryManager.EMPTY));
    }

    @Override
    public NamespacedKey getKey() {
        return CraftNamespacedKey.fromMinecraft(recipe.id());
    }

    @Override
    public void addToCraftingManager() {
        ((IMixinRecipeManager)IMixinMinecraftServer.getServer().getRecipeManager()).addRecipe(recipe);
    }
    */
    
    @Override
    public void addToCraftingManager() {
    	((IMixinRecipeManager)IMixinMinecraftServer.getServer().getRecipeManager()).addRecipe(new RecipeHolder<>(RecipeInterface.toMinecraft(this.getKey()), this.recipe));
    }
    

}

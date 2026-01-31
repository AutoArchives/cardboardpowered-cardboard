package org.cardboardpowered.impl.inventory.recipe;

import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.cardboardpowered.bridge.server.MinecraftServerBridge;
import org.cardboardpowered.bridge.world.item.crafting.RecipeManagerBridge;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ComplexRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.CraftingRecipe;

public class CardboardComplexRecipe extends CraftingRecipe implements CraftRecipe, ComplexRecipe {

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
    	((RecipeManagerBridge) MinecraftServerBridge.getServer().getRecipeManager()).addRecipe(new RecipeHolder<>(CraftRecipe.toMinecraft(this.getKey()), this.recipe));
    }
    

}

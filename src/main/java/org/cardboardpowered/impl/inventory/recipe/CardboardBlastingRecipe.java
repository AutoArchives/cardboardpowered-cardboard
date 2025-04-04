package org.cardboardpowered.impl.inventory.recipe;

import org.cardboardpowered.interfaces.IMixinMinecraftServer;
import org.cardboardpowered.interfaces.IMixinRecipeManager;

import net.minecraft.recipe.RecipeEntry;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.recipe.CookingBookCategory;

public class CardboardBlastingRecipe extends BlastingRecipe implements RecipeInterface {

    public CardboardBlastingRecipe(NamespacedKey key, ItemStack result, RecipeChoice source, float experience, int cookingTime) {
        super(key, result, source, experience, cookingTime);
    }

    public static CardboardBlastingRecipe fromBukkitRecipe(BlastingRecipe recipe) {
        if (recipe instanceof CardboardBlastingRecipe)
            return (CardboardBlastingRecipe) recipe;
        CardboardBlastingRecipe ret = new CardboardBlastingRecipe(recipe.getKey(), recipe.getResult(), recipe.getInputChoice(), recipe.getExperience(), recipe.getCookingTime());
        ret.setGroup(recipe.getGroup());
        return ret;
    }

    @Override
    public void addToCraftingManager() {
        ItemStack result = this.getResult();
        // ((IMixinRecipeManager)IMixinMinecraftServer.getServer().getRecipeManager()).addRecipe(getKey(), new net.minecraft.recipe.BlastingRecipe(this.getGroup(), RecipeInterface.getCategory(this.getCategory()), toNMS(this.getInputChoice(), true), CraftItemStack.asNMSCopy(result), getExperience(), getCookingTime()));
    
        
        ((IMixinRecipeManager)IMixinMinecraftServer.getServer().getRecipeManager()).addRecipe(new net.minecraft.recipe.RecipeEntry<net.minecraft.recipe.BlastingRecipe>(RecipeInterface.toMinecraft(this.getKey()), new net.minecraft.recipe.BlastingRecipe(this.getGroup(), RecipeInterface.getCategory(this.getCategory()), this.toNMS(this.getInputChoice(), true), CraftItemStack.asNMSCopy(result), this.getExperience(), this.getCookingTime())));

    
    }
    
    // TODO: Update API to 1.19.4
    public CookingBookCategory getCategory() {
        return CookingBookCategory.MISC;
    }


}

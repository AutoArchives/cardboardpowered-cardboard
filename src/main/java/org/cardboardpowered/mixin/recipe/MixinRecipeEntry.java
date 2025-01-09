package org.cardboardpowered.mixin.recipe;

import com.javazilla.bukkitfabric.interfaces.IMixinRecipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;

import java.util.Optional;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftMerchantRecipe;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.cardboardpowered.impl.inventory.recipe.CardboardBlastingRecipe;
import org.cardboardpowered.impl.inventory.recipe.CardboardCampfireRecipe;
import org.cardboardpowered.impl.inventory.recipe.CardboardComplexRecipe;
import org.cardboardpowered.impl.inventory.recipe.CardboardFurnaceRecipe;
import org.cardboardpowered.impl.inventory.recipe.CardboardShapedRecipe;
import org.cardboardpowered.impl.inventory.recipe.CardboardShapelessRecipe;
import org.cardboardpowered.impl.inventory.recipe.CardboardSmokingRecipe;
import org.cardboardpowered.impl.inventory.recipe.CardboardStonecuttingRecipe;
import org.cardboardpowered.impl.inventory.recipe.RecipeInterface;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeEntry.class)
public class MixinRecipeEntry implements IMixinRecipe {

	// @Override
	/*
	public final org.bukkit.inventory.Recipe toBukkitRecipe_2() {
		
		RecipeEntry<?> recipeEntry = (RecipeEntry<?>) (Object) this;
		Recipe<?> nmsRecipe = recipeEntry.value();
		
        return ((IMixinRecipe)(Object)recipeEntry).toBukkitRecipe(CraftNamespacedKey.fromMinecraft(recipeEntry.id().getValue()));
    }
    */
	
	// Campfire
    public org.bukkit.inventory.Recipe toBukkitRecipe(CampfireCookingRecipe thiz, NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(thiz.result());
        CardboardCampfireRecipe recipe = new CardboardCampfireRecipe(id, result, RecipeInterface.toBukkit(thiz.ingredient), thiz.experience, thiz.getCookingTime());
        recipe.setGroup(thiz.getGroup());
        recipe.setCategory(RecipeInterface.getCategory(thiz.getCategory()));
        return recipe;
    }
    
    // Blasting
    public org.bukkit.inventory.Recipe toBukkitRecipe(BlastingRecipe thiz, NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(thiz.result);
        CardboardBlastingRecipe recipe = new CardboardBlastingRecipe(id, result, RecipeInterface.toBukkit(thiz.ingredient), thiz.experience, thiz.getCookingTime());
        recipe.setGroup(thiz.getGroup());
        recipe.setCategory(RecipeInterface.getCategory(thiz.getCategory()));
        return recipe;
    }
    
    // Smoking
    public org.bukkit.inventory.Recipe toBukkitRecipe(SmokingRecipe thiz, NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(thiz.result);
        CardboardSmokingRecipe recipe = new CardboardSmokingRecipe(id, result, RecipeInterface.toBukkit(thiz.ingredient), thiz.experience, thiz.getCookingTime());
        recipe.setGroup(thiz.getGroup());
        recipe.setCategory(RecipeInterface.getCategory(thiz.getCategory()));
        return recipe;
    }
    
    // Stonecutting
    public org.bukkit.inventory.Recipe toBukkitRecipe(StonecuttingRecipe thiz, NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(thiz.result());
        CardboardStonecuttingRecipe recipe = new CardboardStonecuttingRecipe(id, result, RecipeInterface.toBukkit(thiz.ingredient()));
        recipe.setGroup(thiz.getGroup());
        return recipe;
    }

	// SpecialCraftingRecipe
	public org.bukkit.inventory.Recipe toBukkitRecipe(SpecialCraftingRecipe thiz, NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(ItemStack.EMPTY);
        CardboardComplexRecipe recipe = new CardboardComplexRecipe(id, result, thiz);
        recipe.setGroup(thiz.getGroup());
        recipe.setCategory(RecipeInterface.getCategory(thiz.getCategory()));
        return recipe;
    }
	
	@Override
	public org.bukkit.inventory.Recipe toBukkitRecipe() {
		RecipeEntry<?> recipeEntry = (RecipeEntry<?>) (Object) this;
		Recipe<?> nmsRecipe = recipeEntry.value();
		RegistryKey<Recipe<?>> id = recipeEntry.id();

		if(nmsRecipe instanceof BlastingRecipe nms) {
			/*
			CraftItemStack result = CraftItemStack.asCraftMirror(nms.getResult(null));

			CardboardBlastingRecipe recipe = new CardboardBlastingRecipe(CraftNamespacedKey.fromMinecraft(id),
					result,
					RecipeInterface.toBukkit(nms.getIngredients().get(0)),
					nms.experience, nms.getCookingTime());
			recipe.setGroup(nms.getGroup());

			return recipe;
			*/
			return toBukkitRecipe(nms, CraftNamespacedKey.fromMinecraft(id.getValue()));
		} else if(nmsRecipe instanceof CampfireCookingRecipe nms) {
			
			return toBukkitRecipe(nms, CraftNamespacedKey.fromMinecraft(id.getValue()));
			
			/*
			CraftItemStack result = CraftItemStack.asCraftMirror(nms.getResult(null));

			CardboardCampfireRecipe recipe = new CardboardCampfireRecipe(CraftNamespacedKey.fromMinecraft(id),
					result,
					RecipeInterface.toBukkit(nms.getIngredients().get(0)),
					nms.experience, nms.getCookingTime());
			recipe.setGroup(nms.getGroup());

			return recipe;
			*/
		} else if(nmsRecipe instanceof ShapedRecipe nms) {
			CraftItemStack result = CraftItemStack.asCraftMirror(nms.result);
			CardboardShapedRecipe recipe = new CardboardShapedRecipe(id.getValue(), result, nms);
			recipe.setGroup(nms.group);

			switch(nms.getHeight()) {
				case 1:
					switch(nms.getWidth()) {
						case 1:
							recipe.shape("a");
							break;
						case 2:
							recipe.shape("ab");
							break;
						case 3:
							recipe.shape("abc");
							break;
					}
					break;
				case 2:
					switch(nms.getWidth()) {
						case 1:
							recipe.shape("a", "b");
							break;
						case 2:
							recipe.shape("ab", "cd");
							break;
						case 3:
							recipe.shape("abc", "def");
							break;
					}
					break;
				case 3:
					switch(nms.getWidth()) {
						case 1:
							recipe.shape("a", "b", "c");
							break;
						case 2:
							recipe.shape("ab", "cd", "ef");
							break;
						case 3:
							recipe.shape("abc", "def", "ghi");
							break;
					}
					break;
			}
			char c = 'a';
			for(Optional<Ingredient> list : nms.getIngredients()) {
				RecipeChoice choice = RecipeInterface.toBukkit(list);
				
				if (choice != RecipeChoice.empty()) {
					 recipe.setIngredient(c, choice);
				}
				
				// if(choice != null) recipe.setIngredient(c, choice);
				c++;
			}
			return recipe;
		} else if(nmsRecipe instanceof ShapelessRecipe nms) {
			CraftItemStack result = CraftItemStack.asCraftMirror(nms.result);
			CardboardShapelessRecipe recipe = new CardboardShapelessRecipe(id.getValue(), result, nms);
			recipe.setGroup(nms.group);
			for(Ingredient list : nms.ingredients)
				recipe.addIngredient(RecipeInterface.toBukkit(list));
			return recipe;
		} else if(nmsRecipe instanceof SmeltingRecipe nms) {
			CraftItemStack result = CraftItemStack.asCraftMirror(nms.result);

			CardboardFurnaceRecipe recipe = new CardboardFurnaceRecipe(CraftNamespacedKey.fromMinecraft(id.getValue()),
					result,
					RecipeInterface.toBukkit(nms.ingredient()),
					nms.experience, nms.getCookingTime());
			recipe.setGroup(nms.getGroup());

			return recipe;
		} else if(nmsRecipe instanceof SmokingRecipe nms) {
			/*
			CraftItemStack result = CraftItemStack.asCraftMirror(nms.getResult(null));

			CardboardSmokingRecipe recipe = new CardboardSmokingRecipe(CraftNamespacedKey.fromMinecraft(id),
					result,
					RecipeInterface.toBukkit(nms.getIngredients().get(0)),
					nms.experience, nms.getCookingTime());
			recipe.setGroup(nms.group);

			return recipe;
			*/
			return toBukkitRecipe(nms, CraftNamespacedKey.fromMinecraft(id.getValue()));
		} else if(nmsRecipe instanceof StonecuttingRecipe nms) {
			/*
			CraftItemStack result = CraftItemStack.asCraftMirror(nms.getResult(null));

			CardboardStonecuttingRecipe recipe = new CardboardStonecuttingRecipe(
					CraftNamespacedKey.fromMinecraft(id),
					result,
					RecipeInterface.toBukkit(nms.getIngredients().get(0)));
			recipe.setGroup(nms.getGroup());

			return recipe;
			*/
			return toBukkitRecipe(nms, CraftNamespacedKey.fromMinecraft(id.getValue()));
		} else if(nmsRecipe instanceof TradeOffer nms) {
			return new CraftMerchantRecipe(nms);
		} else if(nmsRecipe instanceof SpecialCraftingRecipe nms) {
			
			return toBukkitRecipe(nms, CraftNamespacedKey.fromMinecraft(id.getValue()));
			// return new CardboardComplexRecipe((RecipeEntry<SpecialCraftingRecipe>) recipeEntry);
		} else {
			throw new IllegalArgumentException("Invalid recipe type: " + nmsRecipe.getClass());
		}

	}

}

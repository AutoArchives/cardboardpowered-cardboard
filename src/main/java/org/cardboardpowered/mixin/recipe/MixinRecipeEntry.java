package org.cardboardpowered.mixin.recipe;

import org.cardboardpowered.interfaces.IMixinRecipe;
import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.trading.MerchantOffer;
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
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeHolder.class)
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
        CardboardCampfireRecipe recipe = new CardboardCampfireRecipe(id, result, CraftRecipe.toBukkit(thiz.input), thiz.experience, thiz.cookingTime());
        recipe.setGroup(thiz.group());
        recipe.setCategory(CraftRecipe.getCategory(thiz.category()));
        return recipe;
    }
    
    // Blasting
    public org.bukkit.inventory.Recipe toBukkitRecipe(BlastingRecipe thiz, NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(thiz.result);
        CardboardBlastingRecipe recipe = new CardboardBlastingRecipe(id, result, CraftRecipe.toBukkit(thiz.input), thiz.experience, thiz.cookingTime());
        recipe.setGroup(thiz.group());
        recipe.setCategory(CraftRecipe.getCategory(thiz.category()));
        return recipe;
    }
    
    // Smoking
    public org.bukkit.inventory.Recipe toBukkitRecipe(SmokingRecipe thiz, NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(thiz.result);
        CardboardSmokingRecipe recipe = new CardboardSmokingRecipe(id, result, CraftRecipe.toBukkit(thiz.input), thiz.experience, thiz.cookingTime());
        recipe.setGroup(thiz.group());
        recipe.setCategory(CraftRecipe.getCategory(thiz.category()));
        return recipe;
    }
    
    // Stonecutting
    public org.bukkit.inventory.Recipe toBukkitRecipe(StonecutterRecipe thiz, NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(thiz.result());
        CardboardStonecuttingRecipe recipe = new CardboardStonecuttingRecipe(id, result, CraftRecipe.toBukkit(thiz.input()));
        recipe.setGroup(thiz.group());
        return recipe;
    }

	// SpecialCraftingRecipe
	public org.bukkit.inventory.Recipe toBukkitRecipe(CustomRecipe thiz, NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(ItemStack.EMPTY);
        CardboardComplexRecipe recipe = new CardboardComplexRecipe(id, result, thiz);
        recipe.setGroup(thiz.group());
        recipe.setCategory(CraftRecipe.getCategory(thiz.category()));
        return recipe;
    }
	
	@Override
	public org.bukkit.inventory.Recipe toBukkitRecipe() {
		RecipeHolder<?> recipeEntry = (RecipeHolder<?>) (Object) this;
		Recipe<?> nmsRecipe = recipeEntry.value();
		ResourceKey<Recipe<?>> id = recipeEntry.id();

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
			return toBukkitRecipe(nms, CraftNamespacedKey.fromMinecraft(id.identifier()));
		} else if(nmsRecipe instanceof CampfireCookingRecipe nms) {
			
			return toBukkitRecipe(nms, CraftNamespacedKey.fromMinecraft(id.identifier()));
			
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
			CardboardShapedRecipe recipe = new CardboardShapedRecipe(id.identifier(), result, nms);
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
				RecipeChoice choice = CraftRecipe.toBukkit(list);
				
				if (choice != RecipeChoice.empty()) {
					 recipe.setIngredient(c, choice);
				}
				
				// if(choice != null) recipe.setIngredient(c, choice);
				c++;
			}
			return recipe;
		} else if(nmsRecipe instanceof ShapelessRecipe nms) {
			CraftItemStack result = CraftItemStack.asCraftMirror(nms.result);
			CardboardShapelessRecipe recipe = new CardboardShapelessRecipe(id.identifier(), result, nms);
			recipe.setGroup(nms.group);
			for(Ingredient list : nms.ingredients)
				recipe.addIngredient(CraftRecipe.toBukkit(list));
			return recipe;
		} else if(nmsRecipe instanceof SmeltingRecipe nms) {
			CraftItemStack result = CraftItemStack.asCraftMirror(nms.result);

			CardboardFurnaceRecipe recipe = new CardboardFurnaceRecipe(CraftNamespacedKey.fromMinecraft(id.identifier()),
					result,
					CraftRecipe.toBukkit(nms.input()),
					nms.experience, nms.cookingTime());
			recipe.setGroup(nms.group());

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
			return toBukkitRecipe(nms, CraftNamespacedKey.fromMinecraft(id.identifier()));
		} else if(nmsRecipe instanceof StonecutterRecipe nms) {
			/*
			CraftItemStack result = CraftItemStack.asCraftMirror(nms.getResult(null));

			CardboardStonecuttingRecipe recipe = new CardboardStonecuttingRecipe(
					CraftNamespacedKey.fromMinecraft(id),
					result,
					RecipeInterface.toBukkit(nms.getIngredients().get(0)));
			recipe.setGroup(nms.getGroup());

			return recipe;
			*/
			return toBukkitRecipe(nms, CraftNamespacedKey.fromMinecraft(id.identifier()));
		} else if(nmsRecipe instanceof MerchantOffer nms) {
			return new CraftMerchantRecipe(nms);
		} else if(nmsRecipe instanceof CustomRecipe nms) {
			
			return toBukkitRecipe(nms, CraftNamespacedKey.fromMinecraft(id.identifier()));
			// return new CardboardComplexRecipe((RecipeEntry<SpecialCraftingRecipe>) recipeEntry);
		} else {
			throw new IllegalArgumentException("Invalid recipe type: " + nmsRecipe.getClass());
		}

	}

}

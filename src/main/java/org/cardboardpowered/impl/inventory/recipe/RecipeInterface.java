package org.cardboardpowered.impl.inventory.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.recipe.Ingredient;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.cardboardpowered.interfaces.IIngredient;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public interface RecipeInterface extends Recipe {


    void addToCraftingManager();

    default Optional<Ingredient> toNMSOptional(RecipeChoice bukkit, boolean requireNotEmpty) {
        return (bukkit == null || bukkit == RecipeChoice.empty()) ? Optional.empty() : Optional.of(this.toNMS(bukkit, requireNotEmpty)); // Paper - support "empty" choices
    }

    default Ingredient toNMS(RecipeChoice bukkit, boolean requireNotEmpty) {
        // Paper start
        return toIngredient(bukkit, requireNotEmpty);
    }
    static Ingredient toIngredient(RecipeChoice bukkit, boolean requireNotEmpty) {
        // Paper end
        Ingredient stack;

        if (bukkit == null) {
            stack = Ingredient.ofItems();
        } else if (bukkit instanceof RecipeChoice.MaterialChoice) {
            stack = Ingredient.ofItems(((RecipeChoice.MaterialChoice) bukkit).getChoices().stream().map((mat) -> CraftItemType.bukkitToMinecraft(mat)));
        } else if (bukkit instanceof RecipeChoice.ExactChoice) {
            stack = IIngredient.cb$ofStacks(((RecipeChoice.ExactChoice) bukkit).getChoices().stream().map((mat) -> CraftItemStack.asNMSCopy(mat)).toList());
            // Paper start - support "empty" choices - legacy method that spigot might incorrectly call
            // Their impl of Ingredient.of() will error, ingredients need at least one entry.
            // Callers running into this exception may have passed an incorrect empty() recipe choice to a non-empty slot or
            // spigot calls this method in a wrong place.
        } else if (bukkit == RecipeChoice.empty()) {
            throw new IllegalArgumentException("This ingredient cannot be empty");
            // Paper end - support "empty" choices
        } else {
            throw new IllegalArgumentException("Unknown recipe stack instance " + bukkit);
        }

        if (requireNotEmpty) {
            Preconditions.checkArgument(!stack.isEmpty(), "Recipe requires at least one non-air choice");
        }

        return stack;
    }

    public static RecipeChoice toBukkit(Optional<Ingredient> list) {
        return list.map(RecipeInterface::toBukkit).orElse(RecipeChoice.empty()); // Paper - fix issue with recipe API
    }

    public static RecipeChoice toBukkit(Ingredient list) {
        if (list.isEmpty()) {
            return RecipeChoice.empty(); // Paper - null breaks API contracts
        }
        
        IIngredient cblist = (IIngredient) list;

        if (cblist.cb$isExact()) {
            List<org.bukkit.inventory.ItemStack> choices = new ArrayList<>(cblist.cb$itemStacks().size());
            for (net.minecraft.item.ItemStack i : cblist.cb$itemStacks()) {
                choices.add(CraftItemStack.asBukkitCopy(i));
            }

            return new RecipeChoice.ExactChoice(choices);
        } else {
            List<org.bukkit.Material> choices = list.getMatchingItems().map((i) -> CraftItemType.minecraftToBukkit(i.value())).toList();

            return new RecipeChoice.MaterialChoice(choices);
        }
    }

    public static net.minecraft.recipe.book.CraftingRecipeCategory getCategory(CraftingBookCategory bukkit) {
        return net.minecraft.recipe.book.CraftingRecipeCategory.valueOf(bukkit.name());
    }

    public static CraftingBookCategory getCategory(net.minecraft.recipe.book.CraftingRecipeCategory nms) {
        return CraftingBookCategory.valueOf(nms.name());
    }

    public static net.minecraft.recipe.book.CookingRecipeCategory getCategory(CookingBookCategory bukkit) {
        return net.minecraft.recipe.book.CookingRecipeCategory.valueOf(bukkit.name());
    }

    public static CookingBookCategory getCategory(net.minecraft.recipe.book.CookingRecipeCategory nms) {
        return CookingBookCategory.valueOf(nms.name());
    }

    public static RegistryKey<net.minecraft.recipe.Recipe<?>> toMinecraft(NamespacedKey key) {
        return RegistryKey.of(RegistryKeys.RECIPE, CraftNamespacedKey.toMinecraft(key));
    }

}
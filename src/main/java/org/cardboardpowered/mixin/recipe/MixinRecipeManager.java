package org.cardboardpowered.mixin.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.javazilla.bukkitfabric.interfaces.IMixinRecipeManager;
import com.mojang.serialization.JsonOps;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spigotmc.AsyncCatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

@Mixin(RecipeManager.class)
public class MixinRecipeManager implements IMixinRecipeManager {

	@Shadow
    public Multimap<RecipeType<?>, RecipeEntry<?>> recipesByType = ImmutableMultimap.of();
	
	@Shadow
    private Map<Identifier, RecipeEntry<?>> recipesById = ImmutableMap.of();
	
	@Shadow
	private RegistryWrapper.WrapperLookup registryLookup;
	
	@Override
	public  Multimap<RecipeType<?>, RecipeEntry<?>> cb$get_recipesByType() {
		return recipesByType;
	}
	
    //@Invoker("deserialize")
    //protected static RecipeEntry<?> method_17720(Identifier minecraftkey, JsonObject jsonobject) {
    //    return null;
    //}
	
	
	

    @Shadow public boolean errored;
    // @Shadow public Map<RecipeType<?>, Map<Identifier, RecipeEntry<?>>> recipes = ImmutableMap.of();
    // @Shadow public <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllOfType(RecipeType<T> recipes) {return null;}

    @Unique private static final Logger LOGGER_BF = LogManager.getLogger("Bukkit|RecipeManager");

    @Inject(at = @At("RETURN"), method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V")
    public void cardboard$apply_make_mut(Map<Identifier, JsonElement> map, ResourceManager rm, Profiler gameprofilerfiller, CallbackInfo ci) {
    	this.recipesById = new HashMap<>(this.recipesById);
        this.recipesByType = LinkedHashMultimap.create(this.recipesByType);
        LOGGER_BF.info("Loaded " + this.recipesById.size() + " recipes");
    }
    
    @Inject(method = "setRecipes", at = @At("RETURN"))
    private void cardboard$set_recipes_make_mut(Iterable<RecipeEntry<?>> recipes, CallbackInfo ci) {
    	this.recipesById = new HashMap<>(this.recipesById);
        this.recipesByType = LinkedHashMultimap.create(this.recipesByType);
    }
    
    /*
     * @author BukkitFabric
     * @reason Properly fill recipe map
     *
    @Inject(at = @At("TAIL"), method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V")
    public void apply(Map<Identifier, JsonElement> map, ResourceManager iresourcemanager, Profiler gameprofilerfiller, CallbackInfo ci) {

        this.errored = false;
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        ImmutableMap.Builder com_google_common_collect_immutablemap_builder = ImmutableMap.builder();
        RegistryOps<JsonElement> registryops = this.registryLookup.getOps(JsonOps.INSTANCE);
        for (Map.Entry<Identifier, JsonElement> entry : map.entrySet()) {
            Identifier minecraftkey = entry.getKey();
            try {
                Recipe irecipe = Recipe.CODEC.parse(registryops, entry.getValue()).getOrThrow(JsonParseException::new);
                RecipeEntry<Recipe> recipeholder = new RecipeEntry<Recipe>(minecraftkey, irecipe);
                builder.put(irecipe.getType(), recipeholder);
                com_google_common_collect_immutablemap_builder.put((Object)minecraftkey, recipeholder);
            }
            catch (JsonParseException | IllegalArgumentException jsonparseexception) {
            	LOGGER_BF.error("Parsing error loading recipe {}", (Object)minecraftkey, (Object)jsonparseexception);
            }
        }
        this.recipesByType = LinkedHashMultimap.create(builder.build());
        this.recipesById = Maps.newHashMap(com_google_common_collect_immutablemap_builder.build());
        LOGGER_BF.info("Loaded " + this.recipesById.size() + " recipes");
    }
    */

    @Override
    public void addRecipe(RecipeEntry<?> irecipe) {
        // AsyncCatcher.catchOp("Recipe Add");
        Collection map = this.recipesByType.get(irecipe.value().getType());
        if (this.recipesById.containsKey(irecipe.id())) {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + String.valueOf(irecipe.id()));
        }
        map.add(irecipe);
        this.recipesById.put(irecipe.id(), irecipe);
    }
    
    // @Override
    /*public void addRecipe_old(RecipeEntry<?> entry) {
        Map<Identifier, RecipeEntry<?>> map = this.recipes.get(entry.value().getType());
        if (map.containsKey(entry.id()))
            throw new IllegalStateException("Duplicate recipe ignored with ID " + entry.toString());
        else
            map.put(entry.id(), entry);
    }

    /**
     * @author BukkitFabric
     * @reason Clear when no recipe is found
     */
    // FIXME: 1.18.2
    /*@Overwrite
    public <C extends Inventory, T extends Recipe<C>> Optional<T> getFirstMatch(RecipeType<T> recipes, C c0, World world) {
        Optional<T> recipe = this.getAllOfType(recipes).values().stream().flatMap((irecipe) -> Util.stream(recipes.match(irecipe, world, c0))).findFirst();
        ((IMixinInventory)c0).setCurrentRecipe(recipe.orElse(null)); // Clear recipe when no recipe is found
        return recipe;
    }*/
    
    @Override
    public void clearRecipes() {
        this.recipesByType = LinkedHashMultimap.create();
        this.recipesById = Maps.newHashMap();
    }
    
    @Override
    public boolean removeRecipe(Identifier mcKey) {
        Iterator iter = this.recipesByType.values().iterator();
        while (iter.hasNext()) {
            RecipeEntry recipe = (RecipeEntry)iter.next();
            if (!recipe.id().equals(mcKey)) continue;
            iter.remove();
        }
        return this.recipesById.remove(mcKey) != null;
    }


    /*
    @Override
    public void clearRecipes() {
        this.recipes = Maps.newHashMap();
        for (RecipeType<?> recipeType : Registries.RECIPE_TYPE)
            this.recipes.put(recipeType, new Object2ObjectLinkedOpenHashMap<>());
    }

    @Override
    public Map<RecipeType<?>, Map<Identifier, RecipeEntry<?>>> getRecipes() {
        return recipes;
    }
    */

}

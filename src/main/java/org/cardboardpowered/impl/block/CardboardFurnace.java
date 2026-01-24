package org.cardboardpowered.impl.block;

import net.kyori.adventure.text.Component;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.block.CraftContainer;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceInventory;
import org.cardboardpowered.impl.inventory.CardboardFurnaceInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CardboardFurnace<T extends AbstractFurnaceBlockEntity> extends CraftContainer<T> implements Furnace {

    public CardboardFurnace(World world, T tileEntity) {
        super(world, tileEntity);
    }
    
    @Override
    public abstract CardboardFurnace<T> copy();

    @Override
    public abstract CardboardFurnace<T> copy(Location var1);

    protected CardboardFurnace(CardboardFurnace<T> state, Location location) {
        super(state, location);
    }

    @Override
    public FurnaceInventory getSnapshotInventory() {
        return new CardboardFurnaceInventory(this.getSnapshot());
    }

    @Override
    public FurnaceInventory getInventory() {
        return (!this.isPlaced()) ? this.getSnapshotInventory() : new CardboardFurnaceInventory(this.getTileEntity());
    }

    @Override
    public short getBurnTime() {
        return (short) this.getSnapshot().litTimeRemaining;
    }

    @Override
    public void setBurnTime(short burnTime) {
        this.getSnapshot().litTimeRemaining = burnTime;
        this.data = this.data.setValue(AbstractFurnaceBlock.LIT, burnTime > 0);
    }

    @Override
    public short getCookTime() {
        return (short) this.getSnapshot().cookingTimer;
    }

    @Override
    public void setCookTime(short cookTime) {
        this.getSnapshot().cookingTimer = cookTime;
    }

    @Override
    public int getCookTimeTotal() {
        return this.getSnapshot().cookingTotalTime;
    }

    @Override
    public void setCookTimeTotal(int cookTimeTotal) {
        this.getSnapshot().cookingTotalTime = cookTimeTotal;
    }

    @Override
    public double getCookSpeedMultiplier() {
        // TODO Auto-generated method stub
    	// return this.getSnapshot().cookSpeedMultiplier;
        return 0;
    }

    @Override
    public void setCookSpeedMultiplier(double arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public @Nullable Component customName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void customName(@Nullable Component arg0) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public int getRecipeUsedCount(@NotNull NamespacedKey arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public @NotNull Map<CookingRecipe<?>, Integer> getRecipesUsed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasRecipeUsedCount(@NotNull NamespacedKey arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRecipeUsedCount(@NotNull CookingRecipe<?> furnaceRecipe, int count) {
		// TODO Auto-generated method stub
		/*
		final var location = io.papermc.paper.util.MCUtil.toResourceKey(net.minecraft.registry.RegistryKeys.RECIPE, furnaceRecipe.getKey());
        java.util.Optional<net.minecraft.recipe.RecipeEntry<?>> nmsRecipe = (this.isPlaced() ? this.world.getHandle().getRecipeManager() : net.minecraft.server.MinecraftServer.getServer().getRecipeManager()).get(location);
        com.google.common.base.Preconditions.checkArgument(nmsRecipe.isPresent() && nmsRecipe.get().value() instanceof net.minecraft.recipe.AbstractCookingRecipe, furnaceRecipe.getKey() + " is not recognized as a valid and registered furnace recipe");
        if (count > 0) {
            this.getSnapshot().recipesUsed.put(location, count);
        } else {
            this.getSnapshot().recipesUsed.removeInt(location);
        }
        */
	}

	@Override
	public void setRecipesUsed(@NotNull Map<CookingRecipe<?>, Integer> recipesUsed) {
		/*
		this.getSnapshot().recipesUsed.clear();
        recipesUsed.forEach((recipe, integer) -> {
            if (integer != null) {
                this.setRecipeUsedCount(recipe, integer);
            }
        });
        */
	}

}
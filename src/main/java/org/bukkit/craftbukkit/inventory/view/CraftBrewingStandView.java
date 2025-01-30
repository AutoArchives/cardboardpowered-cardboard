package org.bukkit.craftbukkit.inventory.view;

import com.google.common.base.Preconditions;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.screen.BrewingStandScreenHandler;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.view.BrewingStandView;

public class CraftBrewingStandView extends CraftInventoryView<BrewingStandScreenHandler, BrewerInventory> implements BrewingStandView {

    public CraftBrewingStandView(final HumanEntity player, final BrewerInventory viewing, final BrewingStandScreenHandler container) {
        super(player, viewing, container);
    }

    @Override
    public int getFuelLevel() {
        return ((BrewingStandScreenHandler)this.container).getFuel();
    }

    @Override
    public int getBrewingTicks() {
        return ((BrewingStandScreenHandler)this.container).getBrewTime();
    }

    @Override
    public void setFuelLevel(final int fuelLevel) {
        Preconditions.checkArgument(fuelLevel > 0, "The given fuel level must be greater than 0");
        this.container.setProperty(BrewingStandBlockEntity.FUEL_PROPERTY_INDEX, fuelLevel);
    }

    @Override
    public void setBrewingTicks(final int brewingTicks) {
        Preconditions.checkArgument(brewingTicks > 0, "The given brewing ticks must be greater than 0");
        this.container.setProperty(BrewingStandBlockEntity.BREW_TIME_PROPERTY_INDEX, brewingTicks);
    }

    // Paper start - Add recipeBrewTime
    @Override
    public void setRecipeBrewTime(int recipeBrewTime) {
        com.google.common.base.Preconditions.checkArgument(recipeBrewTime > 0, "recipeBrewTime must be positive");
        ((BrewingStandScreenHandler)this.container).propertyDelegate.set(2, recipeBrewTime);
    }

    @Override
    public int getRecipeBrewTime() {
        return ((BrewingStandScreenHandler)this.container).propertyDelegate.get(2);
    }
    // Paper end - Add recipeBrewTime
}

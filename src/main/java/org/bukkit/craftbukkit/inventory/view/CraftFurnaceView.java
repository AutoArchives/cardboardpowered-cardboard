package org.bukkit.craftbukkit.inventory.view;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.view.FurnaceView;

public class CraftFurnaceView extends CraftInventoryView<AbstractFurnaceScreenHandler, FurnaceInventory> implements FurnaceView {

    public CraftFurnaceView(final HumanEntity player, final FurnaceInventory viewing, final AbstractFurnaceScreenHandler container) {
        super(player, viewing, container);
    }

    @Override
    public float getCookTime() {
        return ((AbstractFurnaceScreenHandler)this.container).getCookProgress();
    }

    @Override
    public float getBurnTime() {
        return ((AbstractFurnaceScreenHandler)this.container).getFuelProgress();
    }

    @Override
    public boolean isBurning() {
        return ((AbstractFurnaceScreenHandler)this.container).isBurning();
    }

    @Override
    public void setCookTime(final int cookProgress, final int cookDuration) {
        this.container.setProperty(AbstractFurnaceBlockEntity.COOK_TIME_PROPERTY_INDEX, cookProgress);
        this.container.setProperty(AbstractFurnaceBlockEntity.COOK_TIME_TOTAL_PROPERTY_INDEX, cookDuration);
    }

    @Override
    public void setBurnTime(final int burnProgress, final int burnDuration) {
        this.container.setProperty(AbstractFurnaceBlockEntity.BURN_TIME_PROPERTY_INDEX, burnProgress);
        this.container.setProperty(AbstractFurnaceBlockEntity.FUEL_TIME_PROPERTY_INDEX, burnDuration);
    }
}

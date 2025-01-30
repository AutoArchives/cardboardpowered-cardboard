package org.bukkit.craftbukkit.inventory.view;

import com.google.common.base.Preconditions;
import net.minecraft.screen.CrafterScreenHandler;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.CrafterInventory;
import org.bukkit.inventory.view.CrafterView;

public class CraftCrafterView extends CraftInventoryView<CrafterScreenHandler, CrafterInventory> implements CrafterView {

    public CraftCrafterView(final HumanEntity player, final CrafterInventory viewing, final CrafterScreenHandler container) {
        super(player, viewing, container);
    }

    @Override
    public boolean isSlotDisabled(final int slot) {
        return ((CrafterScreenHandler) this.container).isSlotDisabled(slot);
    }

    @Override
    public boolean isPowered() {
        return ((CrafterScreenHandler) this.container).isTriggered();
    }

    @Override
    public void setSlotDisabled(final int slot, final boolean disabled) {
        Preconditions.checkArgument(slot >= 0 && slot < 9, "Invalid slot index %s for Crafter", slot);

        ((CrafterScreenHandler) this.container).setSlotEnabled(slot, !disabled);
    }
}

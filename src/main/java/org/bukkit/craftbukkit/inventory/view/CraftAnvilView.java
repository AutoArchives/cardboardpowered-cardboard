package org.bukkit.craftbukkit.inventory.view;

import net.minecraft.screen.AnvilScreenHandler;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.view.AnvilView;
import org.cardboardpowered.impl.inventory.CardboardAnvilInventory;
import org.jetbrains.annotations.Nullable;

import com.javazilla.bukkitfabric.interfaces.IMixinScreenHandler;

import io.papermc.paper.adventure.PaperAdventure;

public class CraftAnvilView extends CraftInventoryView<AnvilScreenHandler, AnvilInventory> implements AnvilView {

    public CraftAnvilView(final HumanEntity player, final AnvilInventory viewing, final AnvilScreenHandler container) {
        super(player, viewing, container);
    }
    
    @Nullable
    @Override
    public String getRenameText() {
        return ((AnvilScreenHandler) this.container).newItemName;
    }

    @Override
    public int getRepairItemCountCost() {
        return ((AnvilScreenHandler) this.container).repairItemUsage;
    }

    @Override
    public int getRepairCost() {
        return ((AnvilScreenHandler) this.container).getLevelCost();
    }

    @Override
    public int getMaximumRepairCost() {
        return getRepairCost();
    	// return ((AnvilScreenHandler) this.container).maximumRepairCost;
    }

    @Override
    public void setRepairItemCountCost(final int cost) {
    	((AnvilScreenHandler) this.container).repairItemUsage = cost;
    }

    @Override
    public void setRepairCost(final int cost) {
    	((AnvilScreenHandler) this.container).levelCost.set(cost);
    }

    @Override
    public void setMaximumRepairCost(final int cost) {
    	// ((AnvilScreenHandler) this.container).maximumRepairCost = cost;
    }

    // Paper start
    @Override
    public boolean bypassesEnchantmentLevelRestriction() {
        return false;
    	// return ((AnvilScreenHandler) this.container).bypassEnchantmentLevelRestriction;
    }

    @Override
    public void bypassEnchantmentLevelRestriction(final boolean bypassEnchantmentLevelRestriction) {
    	// ((AnvilScreenHandler) this.container).bypassEnchantmentLevelRestriction = bypassEnchantmentLevelRestriction;
    }
    // Paper end

    public void updateFromLegacy(CardboardAnvilInventory legacy) {
    	
    	// CraftInventoryAnvil
    	// CardboardAnvilInventory
    	
        if (legacy.isRepairCostSet()) {
            this.setRepairCost(legacy.getRepairCost());
        }

        if (legacy.isRepairCostAmountSet()) {
            this.setRepairItemCountCost(legacy.getRepairCostAmount());
        }

        if (legacy.isMaximumRepairCostSet()) {
            this.setMaximumRepairCost(legacy.getMaximumRepairCost());
        }
    }
}

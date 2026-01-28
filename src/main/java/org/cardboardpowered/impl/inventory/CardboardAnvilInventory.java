package org.cardboardpowered.impl.inventory;

import com.google.common.base.Preconditions;
import org.cardboardpowered.bridge.world.inventory.AnvilMenuBridge;
import java.util.function.Consumer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AnvilMenu;
import org.bukkit.Location;
import org.bukkit.craftbukkit.inventory.CraftResultInventory;
import org.bukkit.craftbukkit.inventory.view.CraftAnvilView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.AnvilInventory;

public class CardboardAnvilInventory extends CraftResultInventory implements AnvilInventory {

	private static final int DEFAULT_REPAIR_COST = 0;
    private static final int DEFAULT_REPAIR_COST_AMOUNT = 0;
    private static final int DEFAULT_MAXIMUM_REPAIR_COST = 40;
	
    private final Location location;
    private final AnvilMenu container;
    
    private int repairCost;
    private int repairCostAmount;
    private int maximumRepairCost;

    public CardboardAnvilInventory(Location location, Container inventory, Container resultInventory, AnvilMenu container) {
        super(inventory, resultInventory);
        this.location = location;
        this.container = container;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String getRenameText() {
        return ((AnvilMenuBridge)container).getNewItemName_BF();
    }

    @Override
    public int getRepairCost() {
        return ((AnvilMenuBridge)container).getLevelCost_BF();
    }

    @Override
    public void setRepairCost(int i) {
        ((AnvilMenuBridge)container).setLevelCost_BF(i);
    }

    @Override
    public int getMaximumRepairCost() {
        return ((AnvilMenuBridge)container).getMaxRepairCost_BF();
    }

    @Override
    public void setMaximumRepairCost(int levels) {
        Preconditions.checkArgument(levels >= 0, "Maximum repair cost must be positive (or 0)");
        ((AnvilMenuBridge)container).setMaxRepairCost_BF(levels);
    }

	@Override
	public int getRepairCostAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRepairCostAmount(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	 /*
     * This method provides the best effort guess on whatever the value could be
     * It is possible these values are wrong given there are more than 1 views of this inventory,
     * however it is a limitation seeing as these anvil values are supposed to be in the Container
     * not the inventory.
     */
    private void syncWithArbitraryViewValue(Consumer<CraftAnvilView> consumer) {
        if (this.getViewers().isEmpty()) {
            return;
        }
        final HumanEntity entity = this.getViewers().get(0);
        if (entity != null && entity.getOpenInventory() instanceof CraftAnvilView cav) {
            consumer.accept(cav);
        }
    }

	public boolean isRepairCostSet() {
        return this.repairCost != DEFAULT_REPAIR_COST;
    }

    public boolean isRepairCostAmountSet() {
        return this.repairCostAmount != DEFAULT_REPAIR_COST_AMOUNT;
    }

    public boolean isMaximumRepairCostSet() {
        return this.maximumRepairCost != DEFAULT_MAXIMUM_REPAIR_COST;
    }

}
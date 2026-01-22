package org.cardboardpowered.impl.block;

import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Campfire;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class CardboardCampfire extends CardboardBlockEntityState<CampfireBlockEntity> implements Campfire {

    public CardboardCampfire(World world, CampfireBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardCampfire(CardboardCampfire state, Location location) {
        super(state, location);
    }
	
    @Override
    public CardboardCampfire copy() {
        return new CardboardCampfire(this, null);
    }

    @Override
    public CardboardCampfire copy(Location location) {
        return new CardboardCampfire(this, location);
    }
    
    /*
    public CardboardCampfire(Block block) {
        super(block, CampfireBlockEntity.class);
    }

    public CardboardCampfire(Material material, CampfireBlockEntity te) {
        super(material, te);
    }
    */

    @Override
    public int getSize() {
        return getSnapshot().getItems().size();
    }

    @Override
    public ItemStack getItem(int index) {
        net.minecraft.world.item.ItemStack item = getSnapshot().getItems().get(index);
        return item.isEmpty() ? null : CraftItemStack.asCraftMirror(item);
    }

    @Override
    public void setItem(int index, ItemStack item) {
        getSnapshot().getItems().set(index, CraftItemStack.asNMSCopy(item));
    }

    @Override
    public int getCookTime(int index) {
        return getSnapshot().cookingProgress[index];
    }

    @Override
    public void setCookTime(int index, int cookTime) {
        getSnapshot().cookingProgress[index] = cookTime;
    }

    @Override
    public int getCookTimeTotal(int index) {
        return getSnapshot().cookingTime[index];
    }

    @Override
    public void setCookTimeTotal(int index, int cookTimeTotal) {
        getSnapshot().cookingTime[index] = cookTimeTotal;
    }

	@Override
	public boolean isCookingDisabled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startCooking() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean startCooking(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stopCooking() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean stopCooking(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
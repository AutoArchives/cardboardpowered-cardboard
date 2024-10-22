package org.cardboardpowered.impl.block;

import net.kyori.adventure.text.Component;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.text.Text;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.craftbukkit.block.CraftContainer;
import org.bukkit.inventory.BrewerInventory;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.cardboardpowered.impl.inventory.CardboardBrewerInventory;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class CardboardBrewingStand extends CraftContainer<BrewingStandBlockEntity> implements BrewingStand {

	public CardboardBrewingStand(World world, BrewingStandBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardBrewingStand(CardboardBrewingStand state, Location location) {
        super(state, location);
    }
    
    @Override
    public CardboardBrewingStand copy() {
        return new CardboardBrewingStand(this, null);
    }

    @Override
    public CardboardBrewingStand copy(Location location) {
        return new CardboardBrewingStand(this, location);
    }
	
    /*
    public CardboardBrewingStand(Block block) {
        super(block, BrewingStandBlockEntity.class);
    }

    public CardboardBrewingStand(final Material material, final BrewingStandBlockEntity te) {
        super(material, te);
    }*/

    @Override
    public BrewerInventory getSnapshotInventory() {
        return new CardboardBrewerInventory(this.getSnapshot());
    }

    @Override
    public BrewerInventory getInventory() {
        return (!this.isPlaced()) ? this.getSnapshotInventory() : new CardboardBrewerInventory(this.getTileEntity());
    }

    @Override
    public int getBrewingTime() {
        return this.getSnapshot().brewTime;
    }

    @Override
    public void setBrewingTime(int brewTime) {
        this.getSnapshot().brewTime = brewTime;
    }

    @Override
    public int getFuelLevel() {
        return this.getSnapshot().fuel;
    }

    @Override
    public void setFuelLevel(int level) {
        this.getSnapshot().fuel = level;
    }

    @Override
    public @Nullable Component customName() {
        // TODO Auto-generated method stub
        return CardboardAdventure.asAdventure(Text.of(super.getCustomName()));
    }

    @Override
    public void customName(@Nullable Component arg0) {
        // TODO Auto-generated method stub
    }

	@Override
	public void setRecipeBrewTime(int recipeBrewTime) {
		((BrewingStandBlockEntity)this.getSnapshot()).brewTime = recipeBrewTime;
	}

	@Override
	public int getRecipeBrewTime() {
		return ((BrewingStandBlockEntity)this.getSnapshot()).brewTime;
	}

}
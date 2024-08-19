package org.cardboardpowered.impl.block;

import net.minecraft.block.entity.FurnaceBlockEntity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class CardboardFurnaceFurnace extends CardboardFurnace<FurnaceBlockEntity> {

    public CardboardFurnaceFurnace(World world, FurnaceBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardFurnaceFurnace(CardboardFurnaceFurnace state, Location location) {
        super(state, location);
    }

    @Override
    public CardboardFurnaceFurnace copy() {
        return new CardboardFurnaceFurnace(this, null);
    }

    @Override
    public CardboardFurnaceFurnace copy(Location location) {
        return new CardboardFurnaceFurnace(this, location);
    }

}
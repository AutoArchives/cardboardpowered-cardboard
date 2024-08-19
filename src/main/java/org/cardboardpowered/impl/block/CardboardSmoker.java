package org.cardboardpowered.impl.block;

import net.minecraft.block.entity.SmokerBlockEntity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Smoker;

public class CardboardSmoker extends CardboardFurnace<SmokerBlockEntity> implements Smoker {

    public CardboardSmoker(World world, SmokerBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardSmoker(CardboardSmoker state, Location location) {
        super(state, location);
    }

    @Override
    public CardboardSmoker copy() {
        return new CardboardSmoker(this, null);
    }

    @Override
    public CardboardSmoker copy(Location location) {
        return new CardboardSmoker(this, location);
    }

}
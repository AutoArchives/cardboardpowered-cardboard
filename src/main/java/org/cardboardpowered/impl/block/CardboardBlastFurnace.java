package org.cardboardpowered.impl.block;

import net.minecraft.block.entity.BlastFurnaceBlockEntity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Block;

public class CardboardBlastFurnace extends CardboardFurnace<BlastFurnaceBlockEntity> implements BlastFurnace {

    public CardboardBlastFurnace(World world, BlastFurnaceBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardBlastFurnace(CardboardBlastFurnace state, Location location) {
        super(state, location);
    }

    @Override
    public CardboardBlastFurnace copy() {
        return new CardboardBlastFurnace(this, null);
    }

    @Override
    public CardboardBlastFurnace copy(Location location) {
        return new CardboardBlastFurnace(this, location);
    }

}

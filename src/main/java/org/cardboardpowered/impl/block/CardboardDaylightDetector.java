package org.cardboardpowered.impl.block;

import net.minecraft.block.entity.DaylightDetectorBlockEntity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.DaylightDetector;

public class CardboardDaylightDetector extends CardboardBlockEntityState<DaylightDetectorBlockEntity> implements DaylightDetector {

    public CardboardDaylightDetector(World world, DaylightDetectorBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardDaylightDetector(CardboardDaylightDetector state, Location location) {
        super(state, location);
    }

    @Override
    public CardboardDaylightDetector copy() {
        return new CardboardDaylightDetector(this, null);
    }

    @Override
    public CardboardDaylightDetector copy(Location location) {
        return new CardboardDaylightDetector(this, location);
    }

}
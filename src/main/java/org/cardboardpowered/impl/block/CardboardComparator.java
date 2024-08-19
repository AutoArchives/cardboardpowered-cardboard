package org.cardboardpowered.impl.block;

import net.minecraft.block.entity.ComparatorBlockEntity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Comparator;

public class CardboardComparator extends CardboardBlockEntityState<ComparatorBlockEntity> implements Comparator {

    /*public CardboardComparator(final Block block) {
        super(block, ComparatorBlockEntity.class);
    }

    public CardboardComparator(final Material material, final ComparatorBlockEntity te) {
        super(material, te);
    }*/
    
    public CardboardComparator(World world, ComparatorBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardComparator(CardboardComparator state, Location location) {
        super(state, location);
    }

    @Override
    public CardboardComparator copy() {
        return new CardboardComparator(this, null);
    }

    @Override
    public CardboardComparator copy(Location location) {
        return new CardboardComparator(this, location);
    }

}

package org.cardboardpowered.impl.block;

import net.minecraft.block.entity.EnderChestBlockEntity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.EnderChest;

public class CardboardEnderchest extends CardboardBlockEntityState<EnderChestBlockEntity> implements EnderChest {

    public CardboardEnderchest(World world, EnderChestBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardEnderchest(CardboardEnderchest state, Location location) {
        super(state, location);
    }
    
    @Override
    public CardboardEnderchest copy() {
        return new CardboardEnderchest(this, null);
    }

    @Override
    public CardboardEnderchest copy(Location location) {
        return new CardboardEnderchest(this, location);
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isOpen() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void open() {
        // TODO Auto-generated method stub
        
    }

}
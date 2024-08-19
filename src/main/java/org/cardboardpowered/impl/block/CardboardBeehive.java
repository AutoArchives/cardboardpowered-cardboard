package org.cardboardpowered.impl.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.util.math.BlockPos;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Beehive;
import org.bukkit.block.Block;
import org.bukkit.entity.Bee;

public class CardboardBeehive extends CardboardBlockEntityState<BeehiveBlockEntity> implements Beehive {

    public CardboardBeehive(World world, BeehiveBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardBeehive(CardboardBeehive state, Location location) {
        super(state, location);
    }
    
    @Override
    public CardboardBeehive copy() {
        return new CardboardBeehive(this, null);
    }

    @Override
    public CardboardBeehive copy(Location location) {
        return new CardboardBeehive(this, location);
    }
	
    /*
    public CardboardBeehive(final Block block) {
        super(block, BeehiveBlockEntity.class);
    }

    public CardboardBeehive(final Material material, final BeehiveBlockEntity te) {
        super(material, te);
    }*/

    @Override
    public Location getFlower() {
        BlockPos flower = getSnapshot().flowerPos;
        return (flower == null) ? null : new Location(getWorld(), flower.getX(), flower.getY(), flower.getZ());
    }

    @Override
    public void setFlower(Location location) {
        getSnapshot().flowerPos = (location == null) ? null : new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public boolean isFull() {
        return getSnapshot().isFullOfBees();
    }

    @Override
    public boolean isSedated() {
        return isPlaced() && getTileEntity().isSmoked();
    }

    @Override
    public int getEntityCount() {
        return getSnapshot().getBeeCount();
    }

    @Override
    public int getMaxEntities() {
        // TODO Bukkit4Fabirc: auto-generated method stub
        return 0;
    }

    @Override
    public void setMaxEntities(int max) {
        // TODO Bukkit4Fabirc: auto-generated method stub
    }

    @Override
    public List<Bee> releaseEntities() {
        List<Bee> bees = new ArrayList<>();
        // TODO Bukkit4Fabirc: auto-generated method stub
        return bees;
    }

    @Override
    public void addEntity(Bee entity) {
        // TODO Bukkit4Fabirc: auto-generated method stub
    }

    @Override
    public void clearEntities() {
        // TODO Auto-generated method stub
        
    }

}
package org.cardboardpowered.impl.block;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlockState;

import org.cardboardpowered.bridge.world.level.LevelBridge;

import me.isaiah.common.cmixin.IMixinBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class CapturedBlockState extends CraftBlockState {

    private final boolean treeBlock;

    public CapturedBlockState(Block block, int flag, boolean treeBlock) {
        super(block, flag);
        this.treeBlock = treeBlock;
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);

        if (this.treeBlock && getType() == Material.BEE_NEST) {
            WorldGenLevel world = this.world.getHandle();
            BlockPos blockposition1 = this.getPosition();
            RandomSource random = world.getRandom();
            BlockEntity block = world.getBlockEntity(blockposition1);

            if (block instanceof BeehiveBlockEntity) {
                BeehiveBlockEntity beehive = (BeehiveBlockEntity) block;
                int j = 2 + random.nextInt(2);
                for (int k = 0; k < j; ++k) {
                	IMixinBlockEntity ie = (IMixinBlockEntity) beehive;
                	ie.IC$add_bee_to_beehive(world.getLevel(), random.nextInt(599));
                }
            }
        }
        return result;
    }

    public static CapturedBlockState getBlockState(Level world, BlockPos pos, int flag) {
        return new CapturedBlockState(((LevelBridge)world).getCraftWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()), flag, false);
    }

    public static CapturedBlockState getTreeBlockState(Level world, BlockPos pos, int flag) {
        return new CapturedBlockState(((LevelBridge)world).getCraftWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()), flag, true);
    }

}
package org.cardboardpowered.impl.block;


import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlockState;

import com.javazilla.bukkitfabric.interfaces.IMixinWorld;

import me.isaiah.common.cmixin.IMixinBlockEntity;

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
            StructureWorldAccess world = this.world.getHandle();
            BlockPos blockposition1 = this.getPosition();
            Random random = world.getRandom();
            BlockEntity block = world.getBlockEntity(blockposition1);

            if (block instanceof BeehiveBlockEntity) {
                BeehiveBlockEntity beehive = (BeehiveBlockEntity) block;
                int j = 2 + random.nextInt(2);
                for (int k = 0; k < j; ++k) {
                	IMixinBlockEntity ie = (IMixinBlockEntity) beehive;
                	ie.IC$add_bee_to_beehive(world.toServerWorld(), random.nextInt(599));
                }
            }
        }
        return result;
    }

    public static CapturedBlockState getBlockState(World world, BlockPos pos, int flag) {
        return new CapturedBlockState(((IMixinWorld)world).getWorldImpl().getBlockAt(pos.getX(), pos.getY(), pos.getZ()), flag, false);
    }

    public static CapturedBlockState getTreeBlockState(World world, BlockPos pos, int flag) {
        return new CapturedBlockState(((IMixinWorld)world).getWorldImpl().getBlockAt(pos.getX(), pos.getY(), pos.getZ()), flag, true);
    }

}
package org.cardboardpowered.mixin.block;

import com.javazilla.bukkitfabric.interfaces.IMixinWorld;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.cardboardpowered.extras.DualBlockList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

//@MixinInfo(events = {"BlockPistonExtendEvent","BlockPistonRetractEvent","BlockPistonEvent"})
@Mixin(PistonBlock.class)
public class MixinPistonBlock {

	/*
    private PistonHandler cardboard_ph;

    @Redirect(at = @At(value = "NEW", target = "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/piston/PistonHandler;"), method = "tryMove")
    public PistonHandler cardboard_storePH(World world, BlockPos pos, Direction dir, boolean retract) {
        return (cardboard_ph = new PistonHandler(world,pos,dir,retract));
    }
    */

    @Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/piston/PistonHandler;getBrokenBlocks()Ljava/util/List;"),
            method = "move", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void cardboard_doPistonEvents(World world, BlockPos pos, Direction dir, boolean retract, CallbackInfoReturnable<Boolean> ci,
    		BlockPos blockPos, PistonHandler helper) {

        final org.bukkit.block.Block bblock = ((IMixinWorld)world).getWorldImpl().getBlockAt(pos.getX(), pos.getY(), pos.getZ());

		// TODO: Fix null
		//if (null == cardboard_ph) {
		//	System.out.println("Debug: MixinPistonBlock: null == cardboard_ph.");
		//	return;
		//}

        final List<BlockPos> moved = helper.getMovedBlocks();
        final List<BlockPos> broken = helper.getBrokenBlocks();

        // Direction enumdirection1 = retract ? helper.pistonDirection : helper.pistonDirection ;
        
        Direction enumdirection1 = retract ? dir : dir.getOpposite();

        List<org.bukkit.block.Block> blocks = new DualBlockList(moved, broken, bblock.getWorld()) {

            @Override
            public int size() {
                return moved.size() + broken.size();
            }

            @Override
            public org.bukkit.block.Block get(int index) {
                if (index >= size() || index < 0)
                    throw new ArrayIndexOutOfBoundsException(index);
                BlockPos pos = (BlockPos) (index < moved.size() ? moved.get(index) : broken.get(index - moved.size()));
                return bblock.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
            }
        };
        BlockPistonEvent event = retract ? new BlockPistonExtendEvent(bblock, blocks, CraftBlock.notchToBlockFace(enumdirection1)) 
                        : new BlockPistonRetractEvent(bblock, blocks, CraftBlock.notchToBlockFace(enumdirection1));
        CraftServer.INSTANCE.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            for (BlockPos b : broken)
                world.updateListeners(b, Blocks.AIR.getDefaultState(), world.getBlockState(b), 3);
            for (BlockPos b : moved) {
                world.updateListeners(b, Blocks.AIR.getDefaultState(), world.getBlockState(b), 3);
                b = b.offset(enumdirection1);
                world.updateListeners(b, Blocks.AIR.getDefaultState(), world.getBlockState(b), 3);
            }
            ci.setReturnValue(false);
            return;
        }
    }

}

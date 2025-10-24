package org.cardboardpowered.mixin.block;

import org.bukkit.craftbukkit.event.CraftEventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LilyPadBlock.class)
public class MixinLilyPadBlock {

	// TODO: EntityInsideBlockEvent
	
	/*
    @Inject(
    		method = "onEntityCollision", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;)Z"),
    		require = 0 
    )
    public void entityChangeBlock_1_21_9(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler ech, CallbackInfo ci) {
        if (CraftEventFactory.callEntityChangeBlockEvent(entity, pos, Blocks.AIR.getDefaultState()).isCancelled()) {
            ci.cancel();
        }
    }
    */
    
    @Inject(
    		method = "onEntityCollision", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;)Z")
    )
    public void entityChangeBlock_1_21_10(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler ech, boolean b, CallbackInfo ci) {
        if (CraftEventFactory.callEntityChangeBlockEvent(entity, pos, Blocks.AIR.getDefaultState()).isCancelled()) {
            ci.cancel();
        }
    }
}

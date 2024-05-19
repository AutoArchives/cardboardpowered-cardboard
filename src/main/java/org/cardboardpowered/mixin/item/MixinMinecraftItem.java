package org.cardboardpowered.mixin.item;

import com.javazilla.bukkitfabric.impl.BukkitEventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MinecartItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = MinecartItem.class, priority = 999)
public class MixinMinecraftItem {

	@Shadow @Final private AbstractMinecartEntity.Type type;

	
    @Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private boolean cardboard$minecart_redirect_vanilla_spawnEntity(ServerWorld instance, Entity entity) {
        return false;
    }
    
    // Lnet/minecraft/world/ModifiableWorld;spawnEntity(Lnet/minecraft/entity/Entity;)Z

    @Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntity(Lnet/minecraft/entity/Entity;)Z"),
            locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void cardboard$minecart_entity_place_event(ItemUsageContext useOnContext, CallbackInfoReturnable<ActionResult> cir,
                                    World level, BlockPos blockPos, BlockState blockState, ItemStack itemStack,
                                    ServerWorld serverLevel, RailShape railShape, double d,
                                    AbstractMinecartEntity abstractMinecart) {
        // CraftBukkit start
        if (BukkitEventFactory.callEntityPlaceEvent(useOnContext, abstractMinecart).isCancelled()) {
            cir.setReturnValue(ActionResult.FAIL);
        }
        // CraftBukkit end
        if (!level.spawnEntity(abstractMinecart)) cir.setReturnValue(ActionResult.PASS); // CraftBukkit
    }
	
    /**
     * @author
     * @reason
     */
    /*@Overwrite
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (!blockState.isIn(BlockTags.RAILS)) {
            return ActionResult.FAIL;
        } else {
            ItemStack itemStack = context.getStack();
            if (!world.isClient) {
                RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock ? (RailShape)blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                double d = 0.0;
                if (railShape.isAscending()) {
                    d = 0.5;
                }

                AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create((ServerWorld) world, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.0625 + d, (double)blockPos.getZ() + 0.5, this.type, itemStack, context.getPlayer());
                if (itemStack.hasCustomName()) {
                    abstractMinecartEntity.setCustomName(itemStack.getName());
                }
                if (BukkitEventFactory.callEntityPlaceEvent(context, abstractMinecartEntity).isCancelled()) {
                    return ActionResult.FAIL;
                }
                if (!world.spawnEntity(abstractMinecartEntity)) {
                    return ActionResult.PASS;
                }

                world.spawnEntity(abstractMinecartEntity);
                world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
            }

            itemStack.decrement(1);
            return ActionResult.success(world.isClient);
        }
    }*/
}

package org.cardboardpowered.mixin.item;

import org.bukkit.craftbukkit.event.CraftEventFactory;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = MinecartItem.class, priority = 999)
public class MixinMinecraftItem {

    @Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private boolean cardboard$minecart_redirect_vanilla_spawnEntity(ServerWorld instance, Entity entity) {
        return false;
    }

    @Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntity(Lnet/minecraft/entity/Entity;)Z"),
            locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void cardboard$minecart_entity_place_event(ItemUsageContext useOnContext, CallbackInfoReturnable<ActionResult> cir,
                                    World level, BlockPos blockPos, BlockState blockState, ItemStack itemStack,
                                    RailShape railShape, double d, Vec3d vec,
                                    AbstractMinecartEntity abstractMinecart, ServerWorld serverLevel) {
        // CraftBukkit start
        if (CraftEventFactory.callEntityPlaceEvent(useOnContext, abstractMinecart).isCancelled()) {
            cir.setReturnValue(ActionResult.FAIL);
        }
        // CraftBukkit end
        if (!level.spawnEntity(abstractMinecart)) cir.setReturnValue(ActionResult.PASS); // CraftBukkit
    }
	
}

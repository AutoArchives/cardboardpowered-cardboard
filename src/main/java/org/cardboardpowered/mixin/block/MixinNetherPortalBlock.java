package org.cardboardpowered.mixin.block;

import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.cardboardpowered.util.MixinInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cardboardpowered.interfaces.IMixinEntity;
import org.cardboardpowered.interfaces.IMixinWorld;

import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 1.21.10 Note: now uses iCommonLib API
 * I forgot about this in 1.21.9
 */
@MixinInfo(events = {"EntityPortalEnterEvent"})
@Mixin(NetherPortalBlock.class)
public class MixinNetherPortalBlock {

	/*
    @Inject(at = @At("HEAD"), method = "onEntityCollision")
    public void callBukkitEvent(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler ech, CallbackInfo ci) {
        if (!entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals(true)) {
            EntityPortalEnterEvent event = new EntityPortalEnterEvent(((IMixinEntity)entity).getBukkitEntity(), new org.bukkit.Location(((IMixinWorld)world).getCraftWorld(), pos.getX(), pos.getY(), pos.getZ()));
            Bukkit.getPluginManager().callEvent(event);
        }
    }
    */

}
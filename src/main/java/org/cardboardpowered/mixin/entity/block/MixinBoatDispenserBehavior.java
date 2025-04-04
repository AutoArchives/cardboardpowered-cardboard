/**
 * The Bukkit for Fabric Project
 * Copyright (C) 2025 Cardboard contributors
 */
package org.cardboardpowered.mixin.entity.block;

import org.cardboardpowered.interfaces.IMixinWorld;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.BoatDispenserBehavior;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BoatDispenserBehavior.class)
public class MixinBoatDispenserBehavior {

    // @Shadow
    // public ItemDispenserBehavior itemDispenser;

    //@Shadow
    // public BoatEntity.Type boatType;

    @Shadow
    private EntityType<? extends AbstractBoatEntity> boatType;

    public ItemStack dispenseSilently(BlockPointer isourceblock, ItemStack itemstack) {
        Direction enumdirection = (Direction) isourceblock.state().get(DispenserBlock.FACING);
        ServerWorld worldserver = isourceblock.world();
        double d0 = isourceblock.pos().getX() + (double) ((float) enumdirection.getOffsetX() * 1.125F);
        double d1 = isourceblock.pos().getY() + (double) ((float) enumdirection.getOffsetY() * 1.125F);
        double d2 = isourceblock.pos().getZ() + (double) ((float) enumdirection.getOffsetZ() * 1.125F);
        BlockPos blockposition = isourceblock.pos().offset(enumdirection);
        double d3;

        // FIXME: 1.18.2
        //if (worldserver.getFluidState(blockposition).isIn((Tag<Fluid>) FluidTags.WATER)) {
            d3 = 1.0D;
        //} else {
        //    if (!worldserver.getBlockState(blockposition).isAir() || !worldserver.getFluidState(blockposition.down()).isIn((Tag<Fluid>) FluidTags.WATER))
        //        return this.itemDispenser.dispense(isourceblock, itemstack);
        //    d3 = 0.0D;
       // }

        ItemStack itemstack1 = itemstack.split(1);
        org.bukkit.block.Block block = ((IMixinWorld)worldserver).getWorldImpl().getBlockAt(isourceblock.pos().getX(), isourceblock.pos().getY(), isourceblock.pos().getZ());
        CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

        BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(d0, d1 + d3, d2));
        // TODO if (!DispenserBlock.eventFired)
            CraftServer.INSTANCE.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            itemstack.increment(1);
            return itemstack;
        }

        if (!event.getItem().equals(craftItem)) {
            itemstack.increment(1);
            // Chain to handler for new item
            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
            DispenserBehavior idispensebehavior = (DispenserBehavior) DispenserBlock.BEHAVIORS.get(eventStack.getItem());
            if (idispensebehavior != DispenserBehavior.NOOP && idispensebehavior != this) {
                idispensebehavior.dispense(isourceblock, eventStack);
                return itemstack;
            }
        }

        // BoatEntity entityboat = new BoatEntity(worldserver, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ());

        AbstractBoatEntity entityboat = this.boatType.create(worldserver, SpawnReason.DISPENSER);
        
        if (null != entityboat) {
        	entityboat.initPosition(event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ());
        	EntityType.copier(worldserver, itemstack, null).accept(entityboat);
        	entityboat.setYaw(enumdirection.getPositiveHorizontalDegrees());
        }

        
        // entityboat.setVariant(this.boatType);
        if (!worldserver.spawnEntity(entityboat)) itemstack.increment(1); // CraftBukkit
        return itemstack;
    }

}

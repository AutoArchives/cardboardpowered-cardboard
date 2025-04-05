package org.cardboardpowered.mixin.block;

import com.google.common.collect.Lists;
import org.cardboardpowered.interfaces.IMixinEntity;
import org.cardboardpowered.interfaces.IMixinWorld;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ShearsDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPointer;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockShearEntityEvent;
import org.cardboardpowered.impl.block.DispenserBlockHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsDispenserBehavior.class)
public class MixinShearsDispenserBehavior {

    // todo: nonstatic
    private static Block cardboard_block;
    private static CraftItemStack cardboard_saved;

    @Inject(at = @At("HEAD"), method = "dispenseSilently")
    protected void cardboard_dispenseSilently(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> ci) {
        cardboard_block = ((IMixinWorld)pointer.world()).getCraftWorld().getBlockAt(pointer.pos().getX(), pointer.pos().getY(), pointer.pos().getZ());
        cardboard_saved = CraftItemStack.asCraftMirror(stack);

        BlockDispenseEvent event = new BlockDispenseEvent(cardboard_block, cardboard_saved.clone(), new org.bukkit.util.Vector(0, 0, 0));
        if (!DispenserBlockHelper.eventFired) Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.setReturnValue(stack);
            return;
        }

        if (!event.getItem().equals(cardboard_saved)) {
            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
            DispenserBehavior idispensebehavior = (DispenserBehavior) DispenserBlock.BEHAVIORS.get(eventStack.getItem());
            if (idispensebehavior != DispenserBehavior.NOOP && idispensebehavior != this) {
                idispensebehavior.dispense(pointer, eventStack);
                ci.setReturnValue(stack);
                return;
            }
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Shearable;sheared(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/sound/SoundCategory;Lnet/minecraft/item/ItemStack;)V"),
            method = "tryShearEntity")
    private static void doEvent(Shearable s, ServerWorld sworld, SoundCategory cat, ItemStack stack) {
    	BlockShearEntityEvent event = callBlockShearEntityEvent((LivingEntity)s, cardboard_block, cardboard_saved, Shearable_generateDefaultDrops());
    	if (!event.isCancelled()) {
           
    		CraftItemStack.asNMSCopy(event.getDrops());
    		s.sheared(sworld, SoundCategory.BLOCKS, stack);
        	// s.sheared(sworld, SoundCategory.BLOCKS, stack, CraftItemStack.asNMSCopy(event.getDrops()));
        	
        	// s.sheared(cat);
        }
    }
    
    private static List<ItemStack> Shearable_generateDefaultDrops() {
        return Collections.emptyList();
    }

    private static BlockShearEntityEvent callBlockShearEntityEvent(Entity animal, org.bukkit.block.Block dispenser, CraftItemStack is, List<net.minecraft.item.ItemStack> drops) {

    	BlockShearEntityEvent bse = new BlockShearEntityEvent(
    			dispenser,
    			((IMixinEntity)animal).getBukkitEntity(),
    			is,
    			Lists.transform(drops, CraftItemStack::asCraftMirror)
    	);
        Bukkit.getPluginManager().callEvent(bse);
        return bse;
    }

}

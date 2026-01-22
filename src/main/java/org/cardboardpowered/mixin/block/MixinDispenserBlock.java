package org.cardboardpowered.mixin.block;

import org.cardboardpowered.interfaces.IMixinDispenserBlock;
import net.minecraft.world.level.block.DispenserBlock;
import org.cardboardpowered.impl.block.DispenserBlockHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenserBlock.class)
public class MixinDispenserBlock implements IMixinDispenserBlock {

    /**
     * @author Cardboard
     * @reason Set event fired to false
     */
    @Inject(
    		at = @At(
    				value = "INVOKE",
    				target = "Lnet/minecraft/world/level/block/DispenserBlock;getDispenseMethod(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/dispenser/DispenseItemBehavior;"
    			),
    		method = "dispenseFrom"
    	)
    public void doBukkit_setEventFired(CallbackInfo ci) {
        DispenserBlockHelper.eventFired = false;
    }

}

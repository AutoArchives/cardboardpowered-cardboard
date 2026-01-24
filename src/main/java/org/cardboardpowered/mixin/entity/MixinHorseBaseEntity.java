package org.cardboardpowered.mixin.entity;

import org.bukkit.entity.AbstractHorse;
import org.bukkit.event.entity.HorseJumpEvent;
import org.cardboardpowered.interfaces.IHorseBaseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.SimpleContainer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.cardboardpowered.interfaces.IMixinEntity;
import org.cardboardpowered.interfaces.IMixinInventory;

@Mixin(net.minecraft.world.entity.animal.equine.AbstractHorse .class)
public class MixinHorseBaseEntity implements IHorseBaseEntity {
    
    @Shadow
    public SimpleContainer inventory;

    @Inject(at = @At("HEAD"), method = "handleStartJump", cancellable = true)
    public void callJumpEvent(int i, CallbackInfo ci) {
        float power = (i >= 90) ? 1.0F : (0.4F + 0.4F * (float) i / 90.0F);

        HorseJumpEvent event = CraftEventFactory.callHorseJumpEvent((net.minecraft.world.entity.animal.equine.AbstractHorse )(Object)this, power);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
    }
    
    @Inject(at = @At("TAIL"), method = "createInventory")
    public void cardboard$setInvOwner(CallbackInfo ci) {
        ((IMixinInventory)inventory).cardboard$setOwner( (AbstractHorse) ((IMixinEntity)(Object)this).getBukkitEntity() );
    }

    @Override
    public SimpleContainer cardboard$get_items() {
        return inventory;
    }

}
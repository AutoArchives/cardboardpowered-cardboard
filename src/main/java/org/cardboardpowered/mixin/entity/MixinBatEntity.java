package org.cardboardpowered.mixin.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Bat;
import org.bukkit.event.entity.BatToggleSleepEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.javazilla.bukkitfabric.interfaces.IMixinEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(BatEntity.class)
public class MixinBatEntity {

    
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/BatEntity;setRoosting(Z)V"),
            method = "mobTick")
    public void mobTick_doBatSleepEvent(BatEntity bat, boolean sleep) {
        if (handleBatToggleSleepEvent((BatEntity)(Object)this, !sleep)) {
            this.setRoosting(sleep);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/BatEntity;setRoosting(Z)V"),
            method = "damage")
    public void damage_doBatSleepEvent(BatEntity bat, boolean sleep, ServerWorld world, DamageSource source, float amount) {
        if (handleBatToggleSleepEvent((BatEntity)(Object)this, true)) {
            this.setRoosting(false);
        }
    }

    @Shadow
    public void setRoosting(boolean b) {}

    // note: 1.21.4: awake is always == true.
    private static boolean handleBatToggleSleepEvent(Entity bat, boolean awake) {
        BatToggleSleepEvent event = new BatToggleSleepEvent((Bat) ((IMixinEntity)bat).getBukkitEntity(), awake);
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled();
    }

}
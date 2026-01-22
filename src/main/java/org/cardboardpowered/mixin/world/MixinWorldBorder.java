package org.cardboardpowered.mixin.world;

import java.util.List;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldBorder.class)
public class MixinWorldBorder {

    @Shadow
    public List<BorderChangeListener> listeners;

    @Inject(at = @At("HEAD"), method = "addListener", cancellable = true)
    public void addListenerBF(BorderChangeListener listener, CallbackInfo ci) {
        if (listeners.contains(listener)) {
            ci.cancel();
            return;
        }
    }

}

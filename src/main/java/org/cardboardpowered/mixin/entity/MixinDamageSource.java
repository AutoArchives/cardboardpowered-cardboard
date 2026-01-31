package org.cardboardpowered.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.damagesource.DamageSource;
import org.cardboardpowered.bridge.world.damagesource.DamageSourceBridge;

@Mixin(DamageSource.class)
public class MixinDamageSource implements DamageSourceBridge {

    private boolean sweep_BF;

    @Override
    public boolean isSweep_BF() {
        return sweep_BF;
    }

    @Override
    public DamageSource sweep_BF() {
        sweep_BF = true;
        return (DamageSource)(Object)this;
    }

}
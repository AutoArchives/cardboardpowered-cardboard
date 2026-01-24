package org.cardboardpowered.interfaces;

import net.minecraft.world.damagesource.DamageSource;

public interface IMixinDamageSource {

    boolean isSweep_BF();

    DamageSource sweep_BF();

}
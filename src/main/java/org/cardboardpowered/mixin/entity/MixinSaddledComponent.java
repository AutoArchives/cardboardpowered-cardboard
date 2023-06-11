/**
 * Cardboard - Bukkit for Fabric
 * Copyright (C) 2023 Cardboard contributors
 */
package org.cardboardpowered.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.javazilla.bukkitfabric.interfaces.IMixinSaddledComponent;

import net.minecraft.entity.SaddledComponent;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;

@Mixin(SaddledComponent.class)
public class MixinSaddledComponent implements IMixinSaddledComponent {

    @Shadow public DataTracker dataTracker;
    @Shadow public TrackedData<Integer> boostTime;
    @Shadow public boolean boosted;
    @Shadow public int boostedTime; // field_23216
    // @Shadow public int currentBoostTime;

    @Override
    public void setBoostTicks(int ticks) {
        this.boosted = true;
        this.boostedTime = 0;
        // this.currentBoostTime = ticks;
        this.dataTracker.set(this.boostTime, ticks);
    }

}

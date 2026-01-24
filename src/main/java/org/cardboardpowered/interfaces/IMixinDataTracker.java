package org.cardboardpowered.interfaces;

import net.minecraft.network.syncher.EntityDataAccessor;

public interface IMixinDataTracker {

    <T> void markDirty(EntityDataAccessor<T> key);
}

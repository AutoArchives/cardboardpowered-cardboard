package org.cardboardpowered.mixin.network;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import org.cardboardpowered.interfaces.IMixinDataTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SynchedEntityData.class)
public abstract class MixinDataTracker implements IMixinDataTracker {

    @Shadow protected abstract <T> SynchedEntityData.DataItem<T> getItem(EntityDataAccessor<T> trackedData);

    @Shadow private boolean isDirty;

    @Override
    public <T> void markDirty(EntityDataAccessor<T> key) {
        SynchedEntityData.DataItem entry = this.getItem(key);
        entry.setDirty(true);
        this.isDirty = true;
    }
}

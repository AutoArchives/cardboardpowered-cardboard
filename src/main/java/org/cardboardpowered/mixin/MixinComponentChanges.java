package org.cardboardpowered.mixin;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;

import org.cardboardpowered.interfaces.IComponentChanges;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentType;

@Mixin(ComponentChanges.Builder.class)
public class MixinComponentChanges implements IComponentChanges {

    @Shadow @Final private Reference2ObjectMap<DataComponentType<?>, Optional<?>> changes;

    @Override
    public void copy(ComponentChanges orig) {
        this.changes.putAll(orig.changedComponents);
    }

    @Override
    public void clear(DataComponentType<?> type) {
        this.changes.remove(type);
    }

    @Override
    public boolean isEmpty() {
        return this.changes.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof ComponentChanges.Builder patch) {
            return this.changes.equals(patch.changes);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.changes.hashCode();
    }
}

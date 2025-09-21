package io.papermc.paper.registry;

import io.papermc.paper.util.Holderable;
import net.minecraft.registry.entry.RegistryEntry;
import org.bukkit.NamespacedKey;

public abstract class HolderableBase<M> implements Holderable<M> {

    protected final RegistryEntry<M> holder;

    protected HolderableBase(RegistryEntry<M> holder) {
        this.holder = holder;
    }

    @Override
    public final RegistryEntry<M> getHolder() {
        return this.holder;
    }

    @Override
    public final M getHandle() {
        return Holderable.super.getHandle();
    }

    public final int hashCode() {
        return Holderable.super.implHashCode();
    }

    public final boolean equals(Object obj) {
        return Holderable.super.implEquals(obj);
    }

    public String toString() {
        return Holderable.super.implToString();
    }

    @Override
    public final NamespacedKey getKey() {
        return Holderable.super.getKey();
    }

}
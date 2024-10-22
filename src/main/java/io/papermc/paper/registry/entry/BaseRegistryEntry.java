package io.papermc.paper.registry.entry;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.entry.RegistryEntry;
import net.minecraft.registry.Registry;
import org.bukkit.Keyed;

public abstract class BaseRegistryEntry<M, B extends Keyed> implements RegistryEntry<M, B> {

    private final net.minecraft.registry.RegistryKey<? extends Registry<M>> minecraftRegistryKey;
    private final RegistryKey<B> apiRegistryKey;

    protected BaseRegistryEntry(net.minecraft.registry.RegistryKey<? extends Registry<M>> minecraftRegistryKey, RegistryKey<B> apiRegistryKey) {
        this.minecraftRegistryKey = minecraftRegistryKey;
        this.apiRegistryKey = apiRegistryKey;
    }

    @Override
    public final net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey() {
        return this.minecraftRegistryKey;
    }

    @Override
    public final RegistryKey<B> apiKey() {
        return this.apiRegistryKey;
    }
}
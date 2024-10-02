package io.papermc.paper.registry.entry;

import io.papermc.paper.registry.RegistryKey;
import net.minecraft.registry.Registry;

public interface RegistryEntryInfo<M, B> {

    public net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey();

    public RegistryKey<B> apiKey();

}
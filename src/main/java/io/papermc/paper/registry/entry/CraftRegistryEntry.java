package io.papermc.paper.registry.entry;

import io.papermc.paper.registry.RegistryHolder;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.entry.BaseRegistryEntry;
import io.papermc.paper.registry.entry.RegistryEntry;
import java.util.function.BiFunction;
import net.minecraft.registry.Registry;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(value=NonNull.class)
public class CraftRegistryEntry<M, B extends Keyed>
extends BaseRegistryEntry<M, B, CraftRegistry<B, M>> {
    private static final BiFunction<NamespacedKey, ApiVersion, NamespacedKey> EMPTY = (namespacedKey, apiVersion) -> namespacedKey;
    protected final Class<?> classToPreload;
    protected final BiFunction<NamespacedKey, M, B> minecraftToBukkit;
    private BiFunction<NamespacedKey, ApiVersion, NamespacedKey> updater = EMPTY;

    protected CraftRegistryEntry(net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey, RegistryKey<B> apiKey, Class<?> classToPreload, BiFunction<NamespacedKey, M, B> minecraftToBukkit) {
        super(mcKey, apiKey);
        this.classToPreload = classToPreload;
        this.minecraftToBukkit = minecraftToBukkit;
    }

    @Override
    public RegistryEntry<M, B, CraftRegistry<B, M>> withSerializationUpdater(BiFunction<NamespacedKey, ApiVersion, NamespacedKey> updater) {
        this.updater = updater;
        return this;
    }

    @Override
    public RegistryHolder<B> createRegistryHolder(Registry<M> nmsRegistry) {
        return new RegistryHolder.Memoized(() -> this.createApiRegistry(nmsRegistry));
    }

    private CraftRegistry<B, M> createApiRegistry(Registry<M> nmsRegistry) {
        return new CraftRegistry<B, M>(this.classToPreload, nmsRegistry, this.minecraftToBukkit, this.updater);
    }
}


package io.papermc.paper.registry.entry;

import io.papermc.paper.registry.RegistryHolder;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.entry.ApiRegistryEntry;
import io.papermc.paper.registry.entry.CraftRegistryEntry;
import io.papermc.paper.registry.entry.RegistryEntryInfo;
import io.papermc.paper.registry.legacy.DelayedRegistryEntry;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.registry.Registry;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(value=NonNull.class)
public interface RegistryEntry<M, B extends Keyed, R extends org.bukkit.Registry<B>> extends RegistryEntryInfo<M, B> {

    public RegistryHolder<B> createRegistryHolder(Registry<M> var1);

    default public RegistryEntry<M, B, R> withSerializationUpdater(BiFunction<NamespacedKey, ApiVersion, NamespacedKey> updater) {
        return this;
    }

    @Deprecated
    default public RegistryEntry<M, B, R> delayed() {
        return new DelayedRegistryEntry(this);
    }

    public static <M, B extends Keyed> RegistryEntry<M, B, CraftRegistry<B, M>> entry(net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey, RegistryKey<B> apiKey, Class<?> classToPreload, BiFunction<NamespacedKey, M, B> minecraftToBukkit) {
        return new CraftRegistryEntry<M, B>(mcKey, apiKey, classToPreload, minecraftToBukkit);
    }

    public static <M, B extends Keyed> RegistryEntry<M, B, org.bukkit.Registry<B>> apiOnly(net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey, RegistryKey<B> apiKey, Supplier<org.bukkit.Registry<B>> apiRegistrySupplier) {
        return new ApiRegistryEntry(mcKey, apiKey, apiRegistrySupplier);
    }

}

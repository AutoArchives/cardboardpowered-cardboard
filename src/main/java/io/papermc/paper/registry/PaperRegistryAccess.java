package io.papermc.paper.registry;

import io.papermc.paper.registry.PaperRegistries;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryAccessHolder;
import io.papermc.paper.registry.RegistryHolder;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.entry.ApiRegistryEntry;
import io.papermc.paper.registry.entry.RegistryEntry;
import io.papermc.paper.registry.legacy.DelayedRegistry;
import io.papermc.paper.registry.legacy.DelayedRegistryEntry;
import io.papermc.paper.registry.legacy.LegacyRegistryIdentifiers;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.structure.Structure;
import org.bukkit.Keyed;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.VisibleForTesting;

@DefaultQualifier(value=NonNull.class)
public class PaperRegistryAccess
implements RegistryAccess {
    private final Map<RegistryKey<?>, RegistryHolder<?>> registries = new ConcurrentHashMap();

    public static PaperRegistryAccess instance() {
        return (PaperRegistryAccess)RegistryAccessHolder.INSTANCE.orElseThrow(() -> new IllegalStateException("No RegistryAccess implementation found"));
    }

    @VisibleForTesting
    public Set<RegistryKey<?>> getLoadedServerBackedRegistries() {
        return this.registries.keySet().stream().filter(registryHolder -> !(PaperRegistries.getEntry(registryHolder) instanceof ApiRegistryEntry)).collect(Collectors.toUnmodifiableSet());
    }
    
    @Deprecated(forRemoval=true)
    public <T extends Keyed> org.bukkit.Registry<T> getRegistry(Class<T> type) {

        RegistryKey<T> registryKey = Objects.requireNonNull(PaperRegistryAccess.byType(type), () -> String.valueOf(type) + " is not a valid registry type");
        RegistryEntry<?, T> entry = PaperRegistries.getEntry(registryKey);
        RegistryHolder<T> registry = (RegistryHolder<T>) this.registries.get(registryKey);
        if (registry != null) {
            return registry.get();
        }
        if (entry instanceof DelayedRegistryEntry) {
            RegistryHolder.Delayed delayedHolder = new RegistryHolder.Delayed();
            this.registries.put((RegistryKey<?>)registryKey, delayedHolder);
            return delayedHolder.get();
        }
        return null;
    }

    public <T extends Keyed> org.bukkit.Registry<T> getRegistry(RegistryKey<T> key) {
        if (PaperRegistries.getEntry(key) == null) {
            throw new NoSuchElementException(String.valueOf(key) + " is not a valid registry key");
        }
        RegistryHolder<?> registryHolder = this.registries.get(key);
        if (registryHolder == null) {
            throw new IllegalArgumentException(String.valueOf(key) + " points to a registry that is not available yet");
        }
        return (org.bukkit.Registry<T>) PaperRegistryAccess.possiblyUnwrap(registryHolder.get());
    }

    private static <T extends Keyed> org.bukkit.Registry<T> possiblyUnwrap(org.bukkit.Registry<T> registry) {
        if (registry instanceof DelayedRegistry) {
            DelayedRegistry delayedRegistry = (DelayedRegistry)registry;
            return delayedRegistry.delegate();
        }
        return registry;
    }

    public <M> void registerReloadableRegistry(net.minecraft.registry.RegistryKey<? extends Registry<M>> resourceKey, Registry<M> registry) {
        this.registerRegistry(resourceKey, registry, true);
    }

    public <M> void registerRegistry(net.minecraft.registry.RegistryKey<? extends Registry<M>> resourceKey, Registry<M> registry) {
        this.registerRegistry(resourceKey, registry, false);
    }

    private <M, B extends Keyed, R extends org.bukkit.Registry<B>> void registerRegistry(net.minecraft.registry.RegistryKey<? extends Registry<M>> resourceKey, Registry<M> registry, boolean replace) {
    	@Nullable RegistryEntry<M, B> entry = PaperRegistries.getEntry(resourceKey);
        if (entry == null) {
            return;
        }
        @Nullable RegistryHolder<?> registryHolder = this.registries.get(entry.apiKey());
        if (registryHolder == null || replace) {
            this.registries.put(entry.apiKey(), entry.createRegistryHolder(registry));
        } else if (registryHolder instanceof RegistryHolder.Delayed && entry instanceof DelayedRegistryEntry) {
            DelayedRegistryEntry delayedEntry = (DelayedRegistryEntry)entry;
            ((RegistryHolder.Delayed)registryHolder).loadFrom(delayedEntry, registry);
        } else {
            throw new IllegalArgumentException(String.valueOf(resourceKey) + " has already been created");
        }
    }

    @Deprecated
    @VisibleForTesting
    public static <T extends Keyed> RegistryKey<T> byType(Class<T> type) {
        return (RegistryKey<T>) LegacyRegistryIdentifiers.CLASS_TO_KEY_MAP.get(type);
    }
}


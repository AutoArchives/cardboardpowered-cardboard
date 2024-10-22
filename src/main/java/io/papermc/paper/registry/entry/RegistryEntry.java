package io.papermc.paper.registry.entry;

import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.RegistryHolder;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.util.Conversions;
import io.papermc.paper.registry.entry.ApiRegistryEntry;
import io.papermc.paper.registry.entry.CraftRegistryEntry;
import io.papermc.paper.registry.entry.RegistryEntryInfo;
import io.papermc.paper.registry.legacy.DelayedRegistryEntry;
import io.papermc.paper.registry.legacy.*;
import io.papermc.paper.registry.entry.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.registry.Registry;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import io.papermc.paper.registry.legacy.DelayedRegistryEntry;

@DefaultQualifier(value=NonNull.class)
public interface RegistryEntry<M, B extends Keyed>
extends RegistryEntryInfo<M, B> {
    public RegistryHolder<B> createRegistryHolder(Registry<M> var1);

    default public RegistryEntry<M, B> withSerializationUpdater(BiFunction<NamespacedKey, ApiVersion, NamespacedKey> updater) {
        return this;
    }

    @Deprecated
    default public RegistryEntry<M, B> delayed() {
        return new DelayedRegistryEntry(this);
    }

    private static <M, B extends Keyed> RegistryEntryInfo<M, B> possiblyUnwrap(RegistryEntryInfo<M, B> entry) {
        RegistryEntryInfo<M, B> registryEntryInfo;
        if (entry instanceof DelayedRegistryEntry) {
            DelayedRegistryEntry delayed = (DelayedRegistryEntry)entry;
            registryEntryInfo = delayed.delegate();
        } else {
            registryEntryInfo = entry;
        }
        return registryEntryInfo;
    }

    public static <M, B extends Keyed> RegistryEntry<M, B> entry(net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey, RegistryKey<B> apiKey, Class<?> classToPreload, BiFunction<NamespacedKey, M, B> minecraftToBukkit) {
        return new CraftRegistryEntry<M, B>(mcKey, apiKey, classToPreload, minecraftToBukkit);
    }

    public static <M, B extends Keyed> RegistryEntry<M, B> apiOnly(net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey, RegistryKey<B> apiKey, Supplier<org.bukkit.Registry<B>> apiRegistrySupplier) {
        return new ApiRegistryEntry(mcKey, apiKey, apiRegistrySupplier);
    }

    public static <M, T extends Keyed, B extends PaperRegistryBuilder<M, T>> RegistryEntry<M, T> modifiable(net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey, RegistryKey<T> apiKey, Class<?> toPreload, BiFunction<NamespacedKey, M, T> minecraftToBukkit, PaperRegistryBuilder.Filler<M, T, B> filler) {
        return new ModifiableRegistryEntry<M, T, B>(mcKey, apiKey, toPreload, minecraftToBukkit, filler);
    }

    public static <M, T extends Keyed, B extends PaperRegistryBuilder<M, T>> RegistryEntry<M, T> writable(net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey, RegistryKey<T> apiKey, Class<?> toPreload, BiFunction<NamespacedKey, M, T> minecraftToBukkit, PaperRegistryBuilder.Filler<M, T, B> filler) {
        return new WritableRegistryEntry<M, T, B>(mcKey, apiKey, toPreload, minecraftToBukkit, filler);
    }

    public static interface Writable<M, T extends Keyed, B extends PaperRegistryBuilder<M, T>>
    extends Modifiable<M, T, B>,
    Addable<M, T, B> {
        public static boolean isWritable(RegistryEntryInfo<?, ?> entry) {
            DelayedRegistryEntry delayed;
            return entry instanceof Writable || entry instanceof DelayedRegistryEntry && (delayed = (DelayedRegistryEntry)entry).delegate() instanceof Writable;
        }

        public static <M, T extends Keyed, B extends PaperRegistryBuilder<M, T>> Writable<M, T, B> asWritable(RegistryEntryInfo<M, T> entry) {
            return (Writable)RegistryEntry.possiblyUnwrap(entry);
        }
    }

    public static interface Addable<M, T extends Keyed, B extends PaperRegistryBuilder<M, T>>
    extends BuilderHolder<M, T, B> {
        
    	/*
    	default public RegistryFreezeEventImpl<T, B> createFreezeEvent(WritableCraftRegistry<M, T, B> writableRegistry, Conversions conversions) {
            return new RegistryFreezeEventImpl(this.apiKey(), writableRegistry.createApiWritableRegistry(conversions), conversions);
        }
        */

        public static boolean isAddable(RegistryEntryInfo<?, ?> entry) {
            DelayedRegistryEntry delayed;
            return entry instanceof Addable || entry instanceof DelayedRegistryEntry && (delayed = (DelayedRegistryEntry)entry).delegate() instanceof Addable;
        }

        public static <M, T extends Keyed, B extends PaperRegistryBuilder<M, T>> Addable<M, T, B> asAddable(RegistryEntryInfo<M, T> entry) {
            return (Addable)RegistryEntry.possiblyUnwrap(entry);
        }
    }

    public static interface Modifiable<M, T, B extends PaperRegistryBuilder<M, T>>
    extends BuilderHolder<M, T, B> {
        public static boolean isModifiable(RegistryEntryInfo<?, ?> entry) {
            DelayedRegistryEntry delayed;
            return entry instanceof Modifiable || entry instanceof DelayedRegistryEntry && (delayed = (DelayedRegistryEntry)entry).delegate() instanceof Modifiable;
        }

        public static <M, T extends Keyed, B extends PaperRegistryBuilder<M, T>> Modifiable<M, T, B> asModifiable(RegistryEntryInfo<M, T> entry) {
            return (Modifiable)RegistryEntry.possiblyUnwrap(entry);
        }

        /*
        default public RegistryEntryAddEventImpl<T, B> createEntryAddEvent(TypedKey<T> key, B initialBuilder, Conversions conversions) {
            return new RegistryEntryAddEventImpl<T, B>(key, initialBuilder, this.apiKey(), conversions);
        }
        */
    }

    public static interface BuilderHolder<M, T, B extends PaperRegistryBuilder<M, T>>
    extends RegistryEntryInfo<M, T> {
        public B fillBuilder(Conversions var1, TypedKey<T> var2, M var3);
    }
}


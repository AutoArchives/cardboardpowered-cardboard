package io.papermc.paper.registry;

import com.mojang.serialization.Lifecycle;
import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.PaperRegistryListenerManager;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.util.Conversions;
import io.papermc.paper.registry.entry.RegistryEntry;
import io.papermc.paper.registry.event.WritableRegistry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.checkerframework.checker.nullness.qual.Nullable;

public class WritableCraftRegistry<M, T extends Keyed, B extends PaperRegistryBuilder<M, T>>
extends CraftRegistry<T, M> {
    private static final RegistryEntryInfo FROM_PLUGIN = new RegistryEntryInfo(Optional.empty(), Lifecycle.experimental());
    private final RegistryEntry.BuilderHolder<M, T, B> entry;
    private final net.minecraft.registry.SimpleRegistry<M> registry;
    private final PaperRegistryBuilder.Factory<M, T, ? extends B> builderFactory;
    private final BiFunction<? super NamespacedKey, M, T> minecraftToBukkit;

    public WritableCraftRegistry(RegistryEntry.BuilderHolder<M, T, B> entry, Class<?> classToPreload, net.minecraft.registry.SimpleRegistry<M> registry, BiFunction<NamespacedKey, ApiVersion, NamespacedKey> serializationUpdater, PaperRegistryBuilder.Factory<M, T, ? extends B> builderFactory, BiFunction<? super NamespacedKey, M, T> minecraftToBukkit) {
        super(classToPreload, registry, null, serializationUpdater);
        this.entry = entry;
        this.registry = registry;
        this.builderFactory = builderFactory;
        this.minecraftToBukkit = minecraftToBukkit;
    }

    public void register(TypedKey<T> key, Consumer<? super B> value, Conversions conversions) {
        RegistryKey<M> resourceKey = RegistryKey.of(this.registry.getKey(), CardboardAdventure.asVanilla(key.key()));
        this.registry.assertNotFrozen(resourceKey);
        B builder = this.newBuilder(conversions, key);
        value.accept(builder);
        PaperRegistryListenerManager.INSTANCE.registerWithListeners(this.registry, RegistryEntry.Modifiable.asModifiable(this.entry), resourceKey, builder, FROM_PLUGIN, conversions);
    }

    @Override
    public final @Nullable T createBukkit(NamespacedKey namespacedKey, @Nullable M minecraft) {
        if (minecraft == null) {
            return null;
        }
        return this.minecraftToBukkit(namespacedKey, minecraft);
    }

    public WritableRegistry<T, B> createApiWritableRegistry(Conversions conversions) {
        return new ApiWritableRegistry(conversions);
    }

    public T minecraftToBukkit(NamespacedKey namespacedKey, M minecraft) {
        return (T)((Keyed)this.minecraftToBukkit.apply((NamespacedKey)namespacedKey, minecraft));
    }

    protected B newBuilder(Conversions conversions, TypedKey<T> key) {
        return this.builderFactory.create(conversions, key);
    }

    public class ApiWritableRegistry
    implements WritableRegistry<T, B> {
        private final Conversions conversions;

        public ApiWritableRegistry(Conversions conversions) {
            this.conversions = conversions;
        }

        public void register(TypedKey<T> key, Consumer<? super B> value) {
            WritableCraftRegistry.this.register(key, value, this.conversions);
        }
    }
}
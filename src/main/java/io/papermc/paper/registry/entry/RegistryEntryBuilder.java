package io.papermc.paper.registry.entry;

import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.RegistryKey;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.registry.Registry;
// import net.minecraft.registry.entry.RegistryEntry;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

public class RegistryEntryBuilder<M, A extends Keyed> { // TODO remove Keyed

    public static <M, A extends Keyed> RegistryEntryBuilder<M, A> start( // TODO remove Keyed
        final net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey,
        final RegistryKey<A> apiKey
    ) {
        return new RegistryEntryBuilder<>(mcKey, apiKey);
    }

    protected final net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey;
    protected final RegistryKey<A> apiKey;

    private RegistryEntryBuilder(final net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey, RegistryKey<A> apiKey) {
        this.mcKey = mcKey;
        this.apiKey = apiKey;
    }

    public RegistryEntry<M, A> apiOnly(final Supplier<org.bukkit.Registry<A>> apiRegistrySupplier) {
        return new ApiRegistryEntry<>(this.mcKey, this.apiKey, apiRegistrySupplier);
    }

    public CraftStage<M, A> craft(final Class<?> classToPreload, final BiFunction<? super NamespacedKey, M, ? extends A> minecraftToBukkit) {
        return new CraftStage<>(this.mcKey, this.apiKey, classToPreload, new RegistryTypeMapper<>(minecraftToBukkit));
    }

    public CraftStage<M, A> craft(final Class<?> classToPreload, final Function<net.minecraft.registry.entry.RegistryEntry<M>, ? extends A> minecraftToBukkit) {
        return new CraftStage<>(this.mcKey, this.apiKey, classToPreload, new RegistryTypeMapper<>(minecraftToBukkit));
    }

    public static final class CraftStage<M, A extends Keyed> extends RegistryEntryBuilder<M, A> { // TODO remove Keyed

        private final Class<?> classToPreload;
        private final RegistryTypeMapper<M, A> minecraftToBukkit;

        private CraftStage(
            final net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey,
            final RegistryKey<A> apiKey,
            final Class<?> classToPreload,
            final RegistryTypeMapper<M, A> minecraftToBukkit
        ) {
            super(mcKey, apiKey);
            this.classToPreload = classToPreload;
            this.minecraftToBukkit = minecraftToBukkit;
        }

        public io.papermc.paper.registry.entry.RegistryEntry<M, A> build() {
            return new CraftRegistryEntry<>(this.mcKey, this.apiKey, this.classToPreload, this.minecraftToBukkit);
        }

        public <B extends PaperRegistryBuilder<M, A>> io.papermc.paper.registry.entry.RegistryEntry<M, A> modifiable(final PaperRegistryBuilder.Filler<M, A, B> filler) {
            return new ModifiableRegistryEntry<>(this.mcKey, this.apiKey, this.classToPreload, this.minecraftToBukkit, filler);
        }

        public <B extends PaperRegistryBuilder<M, A>> io.papermc.paper.registry.entry.RegistryEntry<M, A> addable(final PaperRegistryBuilder.Filler<M, A, B> filler) {
            return new AddableRegistryEntry<>(this.mcKey, this.apiKey, this.classToPreload, this.minecraftToBukkit, filler);
        }

        public <B extends PaperRegistryBuilder<M, A>> io.papermc.paper.registry.entry.RegistryEntry<M, A> writable(final PaperRegistryBuilder.Filler<M, A, B> filler) {
            return new WritableRegistryEntry<>(this.mcKey, this.apiKey, this.classToPreload, this.minecraftToBukkit, filler);
        }
    }
}

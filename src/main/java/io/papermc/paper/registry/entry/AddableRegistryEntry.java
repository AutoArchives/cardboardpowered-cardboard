package io.papermc.paper.registry.entry;

import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.RegistryHolder;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.WritableCraftRegistry;
import io.papermc.paper.registry.data.util.Conversions;
import io.papermc.paper.registry.entry.CraftRegistryEntry;
import io.papermc.paper.registry.entry.RegistryEntry;
import java.util.function.BiFunction;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

public class AddableRegistryEntry<M, T extends Keyed, B extends PaperRegistryBuilder<M, T>>
extends CraftRegistryEntry<M, T>
implements RegistryEntry.Addable<M, T, B> {
    private final PaperRegistryBuilder.Filler<M, T, B> builderFiller;

    protected AddableRegistryEntry(net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey, RegistryKey<T> apiKey, Class<?> classToPreload, BiFunction<NamespacedKey, M, T> minecraftToBukkit, PaperRegistryBuilder.Filler<M, T, B> builderFiller) {
        super(mcKey, apiKey, classToPreload, minecraftToBukkit);
        this.builderFiller = builderFiller;
    }

    private WritableCraftRegistry<M, T, B> createRegistry(Registry<M> registry) {
        return new WritableCraftRegistry<M, T, B>(this, this.classToPreload, (SimpleRegistry)registry, this.updater, this.builderFiller.asFactory(), this.minecraftToBukkit);
    }

    @Override
    public RegistryHolder<T> createRegistryHolder(Registry<M> nmsRegistry) {
        return new RegistryHolder.Memoized(() -> this.createRegistry(nmsRegistry));
    }

    @Override
    public B fillBuilder(Conversions conversions, TypedKey<T> key, M nms) {
        return this.builderFiller.fill(conversions, key, nms);
    }
}
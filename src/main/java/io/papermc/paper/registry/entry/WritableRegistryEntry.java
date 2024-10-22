package io.papermc.paper.registry.entry;

import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.entry.AddableRegistryEntry;
import io.papermc.paper.registry.entry.RegistryEntry;
import java.util.function.BiFunction;
import net.minecraft.registry.Registry;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

public class WritableRegistryEntry<M, T extends Keyed, B extends PaperRegistryBuilder<M, T>>
extends AddableRegistryEntry<M, T, B>
implements RegistryEntry.Writable<M, T, B> {

    protected WritableRegistryEntry(net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey, RegistryKey<T> apiKey, Class<?> classToPreload, BiFunction<NamespacedKey, M, T> minecraftToBukkit, PaperRegistryBuilder.Filler<M, T, B> builderFiller) {
        super(mcKey, apiKey, classToPreload, minecraftToBukkit, builderFiller);
    }

}
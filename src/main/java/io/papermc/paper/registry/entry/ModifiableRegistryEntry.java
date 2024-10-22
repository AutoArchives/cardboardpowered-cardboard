package io.papermc.paper.registry.entry;

import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.util.Conversions;
import io.papermc.paper.registry.entry.CraftRegistryEntry;
import io.papermc.paper.registry.entry.RegistryEntry;
import java.util.function.BiFunction;
import net.minecraft.registry.Registry;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

public class ModifiableRegistryEntry<M, T extends Keyed, B extends PaperRegistryBuilder<M, T>> extends CraftRegistryEntry<M, T> implements RegistryEntry.Modifiable<M, T, B> {

    protected final PaperRegistryBuilder.Filler<M, T, B> builderFiller;

    protected ModifiableRegistryEntry(net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey, RegistryKey<T> apiKey, Class<?> toPreload, BiFunction<NamespacedKey, M, T> minecraftToBukkit, PaperRegistryBuilder.Filler<M, T, B> builderFiller) {
        super(mcKey, apiKey, toPreload, minecraftToBukkit);
        this.builderFiller = builderFiller;
    }

    @Override
    public B fillBuilder(Conversions conversions, TypedKey<T> key, M nms) {
        return this.builderFiller.fill(conversions, key, nms);
    }

}
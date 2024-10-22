package io.papermc.paper.registry.entry;

import io.papermc.paper.registry.RegistryHolder;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.entry.BaseRegistryEntry;
import java.util.function.Supplier;
import net.minecraft.registry.Registry;
import org.bukkit.Keyed;

public class ApiRegistryEntry<M, B extends Keyed> extends BaseRegistryEntry<M, B> {

    private final Supplier<org.bukkit.Registry<B>> registrySupplier;

    protected ApiRegistryEntry(net.minecraft.registry.RegistryKey<? extends Registry<M>> mcKey, RegistryKey<B> apiKey, Supplier<org.bukkit.Registry<B>> registrySupplier) {
        super(mcKey, apiKey);
        this.registrySupplier = registrySupplier;
    }

    @Override
    public RegistryHolder<B> createRegistryHolder(Registry<M> nmsRegistry) {
        return new RegistryHolder.Memoized(this.registrySupplier);
    }

}
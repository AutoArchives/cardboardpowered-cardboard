package org.cardboardpowered;

import java.util.Optional;

import com.mojang.serialization.Lifecycle;

import io.papermc.paper.registry.data.util.Conversions;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.ServerDynamicRegistryType;

public class Registries_Bridge {

    public static final Conversions BUILT_IN_CONVERSIONS = new Conversions(new RegistryOps.RegistryInfoGetter(){

        public <T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> registryRef) {
            Registry<T> registry = ServerDynamicRegistryType.STATIC_REGISTRY_MANAGER.get(registryRef);
            return Optional.of(new RegistryOps.RegistryInfo<T>(registry.getReadOnlyWrapper(), registry.getTagCreatingWrapper(), Lifecycle.experimental()));
        }
    });
	
}

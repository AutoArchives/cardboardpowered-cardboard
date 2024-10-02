package org.cardboardpowered.mixin.registry;

import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.javazilla.bukkitfabric.BukkitFabricMod;

import io.papermc.paper.registry.PaperRegistryAccess;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.util.Identifier;
import net.minecraft.Bootstrap;
import net.minecraft.registry.*;

import net.minecraft.registry.Registries.Initializer;

@Mixin(Registries.class)
public class MixinRegistries {

	@Shadow
    private static Map<Identifier, Supplier<?>> DEFAULT_ENTRIES;

	@Shadow
    private static MutableRegistry<MutableRegistry<?>> ROOT;
	
	/**
	 * @author Cardboard
	 * @reason Implement Paper's "Add RegistryAccess for managing Registries".patch
	 */
	@Overwrite
    private static <T, R extends MutableRegistry<T>> R create(RegistryKey<? extends Registry<T>> key, R registry, Initializer<T> initializer) {
		Bootstrap.ensureBootstrapped(() -> "registry " + String.valueOf(key));
        
        // Cardboard - start
        PaperRegistryAccess.instance().registerRegistry(registry.getKey(), registry);
        // Cardboard - end
        
        Identifier resourceLocation = key.getValue();
        DEFAULT_ENTRIES.put(resourceLocation, () -> initializer.run(registry));
        ROOT.add((RegistryKey) key, registry, RegistryEntryInfo.DEFAULT);
        return registry;
    }
   
}

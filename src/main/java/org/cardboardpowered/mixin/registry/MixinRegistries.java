package org.cardboardpowered.mixin.registry;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.cardboardpowered.CardboardMod;
import com.mojang.serialization.Lifecycle;

import io.papermc.paper.registry.PaperRegistryAccess;
import io.papermc.paper.registry.data.util.Conversions;
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
	
	@Inject(at = @At("HEAD"), method = "Lnet/minecraft/registry/Registries;create(Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/MutableRegistry;Lnet/minecraft/registry/Registries$Initializer;)Lnet/minecraft/registry/MutableRegistry;")
	private static void testtt(RegistryKey key, MutableRegistry registry, Initializer initializer, CallbackInfoReturnable ci) {
		
		Bootstrap.ensureBootstrapped(() -> "registry " + key.getValue());
		PaperRegistryAccess.instance().registerRegistry(registry.getKey(), registry);
	}

	/**
	 * @author Cardboard Mod
	 * @reason PaperRegistryAccess
	 */
	@Overwrite
	public static void bootstrap() {
		cardboard$bootStrap(() -> {});
    }


    private static void cardboard$bootStrap(Runnable runnable) {
    	Registries.REGISTRIES.freeze();
        init();
        runnable.run();
        freezeRegistries();
        validate(Registries.REGISTRIES);
    }
    
    //@Shadow
    //private static void init() {}
    
    /*
    @Inject(at = @At("HEAD"), method = "init")
    private static void init_bukkit() {
    	try {
			Class.forName(org.bukkit.Registry.class.getName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
    }
    */
    
	/**
	 * @author Cardboard Mod
	 * @reason PaperRegistryAccess
	 */
    @Overwrite
    public static void init() {
    	try {
			Class.forName(org.bukkit.Registry.class.getName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
    	
    	DEFAULT_ENTRIES.forEach((id, initializer) -> {
            if (initializer.get() == null) {
                CardboardMod.LOGGER.warning("Unable to bootstrap registry: " + id);
            }
            
            io.papermc.paper.registry.PaperRegistryAccess.instance().lockReferenceHolders(
            		RegistryKey.ofRegistry(id)
            	); // Paper - lock reference holder creation
        });
    }
    
    @Shadow
    private static void freezeRegistries() {
       /*
        * TODO: PaperRegistryListenerManager
    	REGISTRIES.freeze();
        for (Registry registry : REGISTRIES) {
            Registries.resetTagEntries(registry);
            PaperRegistryListenerManager.INSTANCE.runFreezeListeners(registry.getKey(), BUILT_IN_CONVERSIONS);
            registry.freeze();
        }
        */
    }
    
    @Shadow private static <T extends Registry<?>> void validate(Registry<T> registries) {}

	
	/**
	 * @author Cardboard
	 * @reason Implement Paper's "Add RegistryAccess for managing Registries".patch
	 */
	/*
	@Overwrite
    private static <T, R extends MutableRegistry<T>> R create(RegistryKey<? extends Registry<T>> key, R registry, Initializer<T> initializer) {
		
		Bootstrap.ensureBootstrapped(() -> "registry " + key.getValue());
		
		PaperRegistryAccess.instance().registerRegistry(registry.getKey(), registry);
		
		Identifier identifier = key.getValue();
		DEFAULT_ENTRIES.put(identifier, (Supplier)() -> initializer.run(registry));
		ROOT.add((RegistryKey<MutableRegistry<?>>)key, registry, RegistryEntryInfo.DEFAULT);
		return registry;
		
		/*
		Bootstrap.ensureBootstrapped(() -> "registry " + String.valueOf(key));
        
        // Cardboard - start
        PaperRegistryAccess.instance().registerRegistry(registry.getKey(), registry);
        // Cardboard - end
        
        Identifier resourceLocation = key.getValue();
        DEFAULT_ENTRIES.put(resourceLocation, () -> initializer.run(registry));
        ROOT.add((RegistryKey) key, registry, RegistryEntryInfo.DEFAULT);
        return registry;
        *
    }*/
   
}

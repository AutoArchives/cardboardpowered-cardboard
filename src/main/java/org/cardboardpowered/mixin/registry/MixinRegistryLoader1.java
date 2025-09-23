package org.cardboardpowered.mixin.registry;

import java.util.*;
import java.util.Map;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Lifecycle;

import io.papermc.paper.registry.PaperRegistryAccess;
import io.papermc.paper.registry.PaperRegistryListenerManager;
import net.minecraft.registry.RegistryLoader.Loader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.registry.*;
//import net.minecraft.registry.RegistryLoader.Loader;

@Mixin(RegistryLoader.class)
public class MixinRegistryLoader1 {

    @Inject(
    		at = @At(value = "RETURN"),
    		method = "Lnet/minecraft/registry/RegistryLoader;loadFromResource(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/RegistryOps$RegistryInfoGetter;Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V",
    		locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void cardboard$reg_lock_reference_holders (
    		ResourceManager resourceManager,
    		RegistryOps.RegistryInfoGetter infoGetter,
    		MutableRegistry registry,
    		Decoder elementDecoder, Map<RegistryKey<?>, Exception> errors,
    		CallbackInfo ci) {

    	 PaperRegistryAccess.instance().lockReferenceHolders(registry.getKey());
         // PaperRegistryListenerManager.INSTANCE.runFreezeListeners(registry.getKey(), conversions);
    }
    
    /**
     * @author Cardboard
     * @reason Paper: add method to get the value for pre-filling builders in the reg mod API
     */
    @Overwrite
    private static RegistryOps.RegistryInfoGetter createInfoGetter(List<RegistryWrapper.Impl<?>> registries, List<Loader<?>> additionalRegistries) {
        final HashMap<RegistryKey<? extends Registry<?>>, RegistryOps.RegistryInfo<?>> map = new HashMap<>();
        registries.forEach(registry -> map.put(registry.getKey(), createInfo(registry)));
        additionalRegistries.forEach(loader -> map.put(loader.registry().getKey(), createInfo(loader.registry())));
        
        // Cardboard: Paper: providerForBuilders
        RegistryWrapper.WrapperLookup providerForBuilders = RegistryWrapper.WrapperLookup.of(Stream.concat(registries.stream(), additionalRegistries.stream().map(Loader::registry)));
        
        return new RegistryOps.RegistryInfoGetter(){

            @Override
            public <T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> registryRef) {
                return Optional.ofNullable((RegistryOps.RegistryInfo<T>)map.get(registryRef));
            }
            
            // @Override
            public RegistryWrapper.WrapperLookup lookupForValueCopyViaBuilders() {
                return providerForBuilders;
            }
            
        };
    }
    
    @Shadow
    private static <T> RegistryOps.RegistryInfo<T> createInfo(MutableRegistry<T> registry) {
        return new RegistryOps.RegistryInfo<T>(registry, registry.createMutableRegistryLookup(), registry.getLifecycle());
    }

    @Shadow
    private static <T> RegistryOps.RegistryInfo<T> createInfo(RegistryWrapper.Impl<T> registry) {
        return new RegistryOps.RegistryInfo<T>(registry, registry, registry.getLifecycle());
    }
    
}

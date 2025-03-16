package org.cardboardpowered.mixin.registry;

import java.util.Map;



import org.spongepowered.asm.mixin.Mixin;
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
    
}

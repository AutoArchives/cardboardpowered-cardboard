package org.cardboardpowered.mixin.registry;

import java.util.Map;



import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.serialization.Lifecycle;

import io.papermc.paper.registry.PaperRegistryAccess;
import net.minecraft.registry.RegistryLoader.Loader;
import net.minecraft.registry.*;
//import net.minecraft.registry.RegistryLoader.Loader;

@Mixin(RegistryLoader.Entry.class)
public class MixinRegistryLoader {

	/*
    Loader<T> getLoader(Lifecycle lifecycle, Map<RegistryKey<?>, Exception> errors) {

        SimpleRegistry writableRegistry = new SimpleRegistry(this.key, lifecycle);
        PaperRegistryAccess.instance().registerRegistry(this.key, writableRegistry);
        return new Loader(this, writableRegistry, errors);
    }
    */

	// Lnet/minecraft/registry/RegistryLoader$Entry;getLoader(Lcom/mojang/serialization/Lifecycle;Ljava/util/Map;)Lnet/minecraft/registry/RegistryLoader$Loader;
	
    @Inject(
    		at = @At(value = "RETURN"),
    		method = "Lnet/minecraft/registry/RegistryLoader$Entry;getLoader(Lcom/mojang/serialization/Lifecycle;Ljava/util/Map;)Lnet/minecraft/registry/RegistryLoader$Loader;",
    		locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void cardboard$register_paper_registry(Lifecycle lifecycle, Map<RegistryKey<?>, Exception> errors, CallbackInfoReturnable ci, MutableRegistry writableRegistry) {
    	RegistryLoader.Entry thiz = (RegistryLoader.Entry) (Object) this;
    	PaperRegistryAccess.instance().registerRegistry(thiz.key(), writableRegistry);
    	
    }
    
}

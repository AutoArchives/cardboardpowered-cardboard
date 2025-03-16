package org.cardboardpowered.mixin.registry;

import java.util.HashMap;
import java.util.Map;

import org.cardboardpowered.interfaces.ISimpleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.util.Identifier;

@Mixin(SimpleRegistry.class)
public class MixinSimpleRegistry<T> implements ISimpleRegistry<T> {
	
	@Shadow
	private Map<T, RegistryEntry.Reference<T>> intrusiveValueToEntry;
	
	@Shadow
	private boolean frozen;
	
	// Cardboard - Paper - support pre-filling in registry mod API
	public final Map<Identifier, T> temporaryUnfrozenMap = new HashMap<>();

	@Override
	public Map<Identifier, T> cb$temporaryUnfrozenMap() {
		return temporaryUnfrozenMap;
	}
	
	// Cardboard - Paper
	// used to clear intrusive holders from GameEvent, Item, Block, EntityType, and Fluid from unused instances of those types
	@Override
	public void clearIntrusiveHolder(final T instance) {
		if (null != this.intrusiveValueToEntry) {
			this.intrusiveValueToEntry.remove(instance);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "freeze")
	public void cb$paper_clear_unfrozen_map(CallbackInfoReturnable<net.minecraft.registry.Registry> ci) {
		if (!this.frozen) {
			 this.temporaryUnfrozenMap.clear(); // Paper - support pre-filling in registry mod API
		}
	}
	
	@Inject(at = @At("RETURN"), method = "add")
	public void cb$paper_unfrozen_map(RegistryKey<T> key, T value, RegistryEntryInfo info, CallbackInfoReturnable<RegistryEntry.Reference> ci) {
		// Lnet/minecraft/registry/MutableRegistry;add(Lnet/minecraft/registry/RegistryKey;Ljava/lang/Object;Lnet/minecraft/registry/entry/RegistryEntryInfo;)Lnet/minecraft/registry/entry/RegistryEntry$Reference;
	
		 this.temporaryUnfrozenMap.put(key.getValue(), value); // Paper - support pre-filling in registry mod API
	}
	
}

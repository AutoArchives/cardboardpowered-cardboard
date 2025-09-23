package org.cardboardpowered.mixin.registry;

import java.util.Optional;
import java.util.stream.Stream;

import org.cardboardpowered.interfaces.IRegistryWrapperImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.google.common.base.Predicate;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.Impl;
import net.minecraft.registry.entry.RegistryEntry;

@Mixin(RegistryWrapper.Impl.class)
public interface MixinWrapperImpl<T> extends IRegistryWrapperImpl<T> {

	/**
	 */
	@Override
	public Optional<T> getValueForCopying(RegistryKey<T> var1);
	
	/**
	 * @author Cardboard
	 * @reason getValueForCopying
	 */
	/*
	@Overwrite(remap = false)
	default public Impl<T> method_56882(Predicate<T> predicate) {
        return new Impl.Delegating<T>(){

            // @Override
            public Optional<T> getValueForCopying(RegistryKey<T> resourceKey) {
                return ( (IRegistryWrapperImpl) this.getBase() ) .getValueForCopying(resourceKey).filter(predicate);
            }

            @Override
            public Impl<T> getBase() {
                return this;
            }

            @Override
            public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
                return this.getBase().getOptional(key).filter(entry -> predicate.test(entry.value()));
            }

            @Override
            public Stream<RegistryEntry.Reference<T>> streamEntries() {
                return this.getBase().streamEntries().filter(entry -> predicate.test(entry.value()));
            }
        };
    }
    */
}

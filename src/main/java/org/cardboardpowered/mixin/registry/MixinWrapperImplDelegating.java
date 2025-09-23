package org.cardboardpowered.mixin.registry;

import java.util.Optional;

import org.cardboardpowered.interfaces.IRegistryWrapperImpl;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper.Impl;

@Mixin(Impl.Delegating.class)
public interface MixinWrapperImplDelegating<T> extends IRegistryWrapperImpl<T> {

	@Override
    default public Optional<T> getValueForCopying(RegistryKey<T> resourceKey) {
        return ( (IRegistryWrapperImpl) ( (Impl.Delegating) (Object) this ).getBase() ).getValueForCopying(resourceKey);
    }
	
}

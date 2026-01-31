package org.cardboardpowered.mixin.registry;

import java.util.Optional;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.resources.ResourceKey;
import org.cardboardpowered.bridge.core.RegistryLookupBridge;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RegistryLookup.Delegate.class)
public interface MixinWrapperImplDelegating<T> extends RegistryLookupBridge<T> {

	@Override
    default public Optional<T> getValueForCopying(ResourceKey<T> resourceKey) {
        return ( (RegistryLookupBridge) ( (RegistryLookup.Delegate) (Object) this ).parent() ).getValueForCopying(resourceKey);
    }
	
}

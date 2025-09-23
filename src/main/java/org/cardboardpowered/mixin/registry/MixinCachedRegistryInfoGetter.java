package org.cardboardpowered.mixin.registry;

import org.cardboardpowered.interfaces.IRegistryInfoGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;

@Mixin(RegistryOps.CachedRegistryInfoGetter.class)
public class MixinCachedRegistryInfoGetter implements IRegistryInfoGetter {

	@Shadow
	private RegistryWrapper.WrapperLookup registries;
	
	@Override
    public RegistryWrapper.WrapperLookup lookupForValueCopyViaBuilders() {
        return this.registries;
    }
	
}

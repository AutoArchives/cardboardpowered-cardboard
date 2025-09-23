package org.cardboardpowered.mixin.registry;

import org.cardboardpowered.interfaces.IRegistryInfoGetter;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryOps.RegistryInfoGetter;

@Mixin(RegistryInfoGetter.class)
public interface MixinRegistryInfoGetter extends IRegistryInfoGetter {

	/**
	 */
	@Override
	public RegistryWrapper.WrapperLookup lookupForValueCopyViaBuilders();
	
}
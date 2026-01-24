package org.cardboardpowered.mixin.registry;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps.RegistryInfoLookup;
import org.cardboardpowered.interfaces.IRegistryInfoGetter;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RegistryInfoLookup.class)
public interface MixinRegistryInfoGetter extends IRegistryInfoGetter {

	/**
	 */
	@Override
	public HolderLookup.Provider lookupForValueCopyViaBuilders();
	
}
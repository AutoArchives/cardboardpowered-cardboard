package org.cardboardpowered.mixin.registry;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import org.cardboardpowered.interfaces.IRegistryInfoGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RegistryOps.HolderLookupAdapter.class)
public class MixinCachedRegistryInfoGetter implements IRegistryInfoGetter {

	@Shadow
	private HolderLookup.Provider lookupProvider;
	
	@Override
    public HolderLookup.Provider lookupForValueCopyViaBuilders() {
        return this.lookupProvider;
    }
	
}

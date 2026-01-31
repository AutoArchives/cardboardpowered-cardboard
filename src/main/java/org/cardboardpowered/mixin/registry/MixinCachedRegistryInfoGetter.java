package org.cardboardpowered.mixin.registry;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import org.cardboardpowered.bridge.resources.RegistryInfoLookupBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RegistryOps.HolderLookupAdapter.class)
public class MixinCachedRegistryInfoGetter implements RegistryInfoLookupBridge {

	@Shadow
	private HolderLookup.Provider lookupProvider;
	
	@Override
    public HolderLookup.Provider lookupForValueCopyViaBuilders() {
        return this.lookupProvider;
    }
	
}

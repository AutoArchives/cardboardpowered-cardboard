package io.papermc.paper.world.flag;

import net.minecraft.world.flag.FeatureElement;

public interface PaperFeatureDependent extends FeatureDependant {

    public <M extends FeatureElement> M getHandle();

    /*
    default public @NonNull @Unmodifiable Set<FeatureFlag> requiredFeatures() {
        return PaperFeatureFlagProviderImpl.fromNms(this.getHandle().getRequiredFeatures());
    }
    */

}

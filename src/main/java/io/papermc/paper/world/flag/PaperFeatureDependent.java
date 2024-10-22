package io.papermc.paper.world.flag;

//import io.papermc.paper.world.flag.PaperFeatureFlagProviderImpl;
import net.minecraft.resource.featuretoggle.ToggleableFeature;

public interface PaperFeatureDependent extends FeatureDependant {

    public <M extends ToggleableFeature> M getHandle();

    /*
    default public @NonNull @Unmodifiable Set<FeatureFlag> requiredFeatures() {
        return PaperFeatureFlagProviderImpl.fromNms(this.getHandle().getRequiredFeatures());
    }
    */

}

package io.papermc.paper.registry.data;

import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.data.client.ClientTextureAsset;
import io.papermc.paper.registry.data.util.Checks;
import io.papermc.paper.registry.data.util.Conversions;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.spawn.SpawnConditionSelectors;
import net.minecraft.util.AssetInfo;
import org.bukkit.entity.Frog;
import org.jspecify.annotations.Nullable;

public class PaperFrogVariantRegistryEntry implements FrogVariantRegistryEntry {

    protected @Nullable AssetInfo clientTextureAsset;
    protected SpawnConditionSelectors spawnConditions;
    protected final Conversions conversions;

    public PaperFrogVariantRegistryEntry(Conversions conversions, @Nullable FrogVariant internal) {
        this.conversions = conversions;
        if (internal == null) {
            this.spawnConditions = SpawnConditionSelectors.EMPTY;
            return;
        }
        this.clientTextureAsset = internal.assetInfo();
        this.spawnConditions = internal.spawnConditions();
    }

    public ClientTextureAsset clientTextureAsset() {
        return this.conversions.asBukkit(Checks.asConfigured(this.clientTextureAsset, "clientTextureAsset"));
    }

    public static final class PaperBuilder
    extends PaperFrogVariantRegistryEntry
    implements FrogVariantRegistryEntry.Builder,
    PaperRegistryBuilder<FrogVariant, Frog.Variant> {
        public PaperBuilder(Conversions conversions, @Nullable FrogVariant internal) {
            super(conversions, internal);
        }

        public FrogVariantRegistryEntry.Builder clientTextureAsset(ClientTextureAsset clientTextureAsset) {
            this.clientTextureAsset = this.conversions.asVanilla(Checks.asArgument(clientTextureAsset, "clientTextureAsset"));
            return this;
        }

        @Override
        public FrogVariant build() {
            return new FrogVariant(Checks.asConfigured(this.clientTextureAsset, "clientTextureAsset"), Checks.asConfigured(this.spawnConditions, "spawnConditions"));
        }
    }

}
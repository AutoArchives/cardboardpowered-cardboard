package io.papermc.paper.registry.data;

import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.data.CatTypeRegistryEntry;
import io.papermc.paper.registry.data.client.ClientTextureAsset;
import io.papermc.paper.registry.data.util.Checks;
import io.papermc.paper.registry.data.util.Conversions;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.spawn.SpawnConditionSelectors;
import net.minecraft.util.AssetInfo;
import org.bukkit.entity.Cat;
import org.jspecify.annotations.Nullable;

public class PaperCatTypeRegistryEntry
implements CatTypeRegistryEntry {
    protected @Nullable AssetInfo clientTextureAsset;
    protected SpawnConditionSelectors spawnConditions;
    protected final Conversions conversions;

    public PaperCatTypeRegistryEntry(Conversions conversions, @Nullable CatVariant internal) {
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
    extends PaperCatTypeRegistryEntry
    implements CatTypeRegistryEntry.Builder,
    PaperRegistryBuilder<CatVariant, Cat.Type> {
        public PaperBuilder(Conversions conversions, @Nullable CatVariant internal) {
            super(conversions, internal);
        }

        public CatTypeRegistryEntry.Builder clientTextureAsset(ClientTextureAsset clientTextureAsset) {
            this.clientTextureAsset = this.conversions.asVanilla(Checks.asArgument(clientTextureAsset, "clientTextureAsset"));
            return this;
        }

        @Override
        public CatVariant build() {
            return new CatVariant(Checks.asConfigured(this.clientTextureAsset, "clientTextureAsset"), Checks.asConfigured(this.spawnConditions, "spawnConditions"));
        }
    }
}


package io.papermc.paper.registry.data.util;

import com.google.common.base.Preconditions;
import com.mojang.serialization.JavaOps;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.registry.PaperRegistries;
import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.PaperRegistryBuilderFactory;
import io.papermc.paper.registry.data.client.ClientTextureAsset;
import io.papermc.paper.registry.entry.RegistryEntryMeta;
// import io.papermc.paper.adventure.WrapperAwareSerializer;
import net.kyori.adventure.text.Component;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.AssetInfo;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.Contract;

import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Keyed;
import org.bukkit.craftbukkit.CraftRegistry;
import org.cardboardpowered.adventure.WrapperAwareSerializer;
import org.cardboardpowered.interfaces.IRegistryInfoGetter;
import org.cardboardpowered.interfaces.IRegistryWrapperImpl;

@DefaultQualifier(value=NonNull.class)
public class Conversions {

	private static @Nullable Conversions globalInstance;
    public static Conversions global() {
        if (globalInstance == null) {
            final DynamicRegistryManager globalAccess = CraftRegistry.getMinecraftRegistry();
            // Preconditions.checkState(globalAccess != null, "Global registry access is not available");
            globalInstance = new Conversions(new RegistryOps.RegistryInfoGetter() {
                @Override
                public <T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(final RegistryKey<? extends Registry<? extends T>> registryRef) {
                    final Registry<T> registry = globalAccess.getOrThrow(registryRef);
                    return Optional.of(
                        new RegistryOps.RegistryInfo<>(registry, registry, registry.getLifecycle())
                    );
                }
            });
        }
        return globalInstance;
    }
	
    private final RegistryOps.RegistryInfoGetter lookup;
    private final WrapperAwareSerializer serializer;

    public Conversions(RegistryOps.RegistryInfoGetter lookup) {
        this.lookup = lookup;
        this.serializer = new WrapperAwareSerializer(() -> RegistryOps.of(JavaOps.INSTANCE, lookup));
    }

    public RegistryOps.RegistryInfoGetter lookup() {
        return this.lookup;
    }

    @Contract(value="null -> null; !null -> !null")
    public @Nullable Text asVanilla(@Nullable Component adventure) {
        if (adventure == null) {
            return null;
        }
        return this.serializer.serialize(adventure);
    }

    public Component asAdventure(@Nullable Text vanilla) {
        return vanilla == null ? Component.empty() : this.serializer.deserialize(vanilla);
    }
    
    public ClientTextureAsset asBukkit(@Nullable AssetInfo.TextureAsset clientTextureAsset) {
        return clientTextureAsset == null
           ? null
           : ClientTextureAsset.clientTextureAsset(
              PaperAdventure.asAdventure(clientTextureAsset.id()), PaperAdventure.asAdventure(clientTextureAsset.texturePath())
           );
     }
    
    public AssetInfo.TextureAssetInfo asVanilla(@Nullable ClientTextureAsset clientTextureAsset) {
        return clientTextureAsset == null
           ? null
           : new AssetInfo.TextureAssetInfo(PaperAdventure.asVanilla(clientTextureAsset.identifier()), PaperAdventure.asVanilla(clientTextureAsset.texturePath()));
     }
    
    /*
    public ClientTextureAsset asBukkit(@Nullable AssetInfo clientTextureAsset) {
        return clientTextureAsset == null ? null : ClientTextureAsset.clientTextureAsset(PaperAdventure.asAdventure(clientTextureAsset.id()), PaperAdventure.asAdventure(clientTextureAsset.texturePath()));
    }

    public AssetInfo asVanilla(@Nullable ClientTextureAsset clientTextureAsset) {
        return clientTextureAsset == null ? null : new AssetInfo(PaperAdventure.asVanilla(clientTextureAsset.identifier()), PaperAdventure.asVanilla(clientTextureAsset.texturePath()));
    }
    */
    
    private static <M, A extends Keyed, B extends PaperRegistryBuilder<M, A>> RegistryEntryMeta.Buildable<M, A, B> getDirectHolderBuildableMeta(io.papermc.paper.registry.RegistryKey<A> registryKey) {
        RegistryEntryMeta.Buildable<M, A, B> buildableMeta = PaperRegistries.getBuildableMeta(registryKey);
        Preconditions.checkArgument(buildableMeta.registryTypeMapper().supportsDirectHolders(), "Registry type mapper must support direct holders");
        return buildableMeta;
    }

    public <M, A extends Keyed, B extends PaperRegistryBuilder<M, A>> A createApiInstanceFromBuilder(io.papermc.paper.registry.RegistryKey<A> registryKey, Consumer<? super PaperRegistryBuilderFactory<M, A, B>> value) {
        RegistryEntryMeta.Buildable<M, A, B> meta = Conversions.getDirectHolderBuildableMeta(registryKey);
        PaperRegistryBuilderFactory<M, A, B> builderFactory = this.createRegistryBuilderFactory(registryKey, meta);
        value.accept(builderFactory);
        return meta.registryTypeMapper().createBukkit(net.minecraft.registry.entry.RegistryEntry.of(builderFactory.requireBuilder().build()));
    }
    
    private <M, A extends Keyed, B extends PaperRegistryBuilder<M, A>> PaperRegistryBuilderFactory<M, A, B> createRegistryBuilderFactory(io.papermc.paper.registry.RegistryKey<A> registryKey, RegistryEntryMeta.Buildable<M, A, B> buildableMeta) {
        net.minecraft.registry.RegistryKey<? extends Registry<M>> resourceRegistryKey = PaperRegistries.registryToNms(registryKey);
        RegistryEntryLookup<M> lookupForBuilders = ( (IRegistryInfoGetter)  this.lookup).lookupForValueCopyViaBuilders().getOrThrow(resourceRegistryKey);
        return new PaperRegistryBuilderFactory<>(
        		resourceRegistryKey,
        		this,
        		buildableMeta.builderFiller(),
        		( (IRegistryWrapperImpl) ((RegistryWrapper.Impl)lookupForBuilders) )::getValueForCopying
        );
    }



}
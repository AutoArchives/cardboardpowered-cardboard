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
import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
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
            final RegistryAccess globalAccess = CraftRegistry.getMinecraftRegistry();
            // Preconditions.checkState(globalAccess != null, "Global registry access is not available");
            globalInstance = new Conversions(new RegistryOps.RegistryInfoLookup() {
                @Override
                public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(final ResourceKey<? extends Registry<? extends T>> registryRef) {
                    final Registry<T> registry = globalAccess.lookupOrThrow(registryRef);
                    return Optional.of(
                        new RegistryOps.RegistryInfo<>(registry, registry, registry.registryLifecycle())
                    );
                }
            });
        }
        return globalInstance;
    }
	
    private final RegistryOps.RegistryInfoLookup lookup;
    private final WrapperAwareSerializer serializer;

    public Conversions(RegistryOps.RegistryInfoLookup lookup) {
        this.lookup = lookup;
        this.serializer = new WrapperAwareSerializer(() -> RegistryOps.create(JavaOps.INSTANCE, lookup));
    }

    public RegistryOps.RegistryInfoLookup lookup() {
        return this.lookup;
    }

    @Contract(value="null -> null; !null -> !null")
    public net.minecraft.network.chat.@Nullable Component asVanilla(@Nullable Component adventure) {
        if (adventure == null) {
            return null;
        }
        return this.serializer.serialize(adventure);
    }

    public Component asAdventure(net.minecraft.network.chat.@Nullable Component vanilla) {
        return vanilla == null ? Component.empty() : this.serializer.deserialize(vanilla);
    }
    
    public ClientTextureAsset asBukkit(ClientAsset.Texture clientTextureAsset) {
        return clientTextureAsset == null
           ? null
           : ClientTextureAsset.clientTextureAsset(
              PaperAdventure.asAdventure(clientTextureAsset.id()), PaperAdventure.asAdventure(clientTextureAsset.texturePath())
           );
     }
    
    public ClientAsset.ResourceTexture asVanilla(@Nullable ClientTextureAsset clientTextureAsset) {
        return clientTextureAsset == null
           ? null
           : new ClientAsset.ResourceTexture(PaperAdventure.asVanilla(clientTextureAsset.identifier()), PaperAdventure.asVanilla(clientTextureAsset.texturePath()));
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
        return meta.registryTypeMapper().createBukkit(net.minecraft.core.Holder.direct(builderFactory.requireBuilder().build()));
    }
    
    private <M, A extends Keyed, B extends PaperRegistryBuilder<M, A>> PaperRegistryBuilderFactory<M, A, B> createRegistryBuilderFactory(io.papermc.paper.registry.RegistryKey<A> registryKey, RegistryEntryMeta.Buildable<M, A, B> buildableMeta) {
        net.minecraft.resources.ResourceKey<? extends Registry<M>> resourceRegistryKey = PaperRegistries.registryToNms(registryKey);
        HolderGetter<M> lookupForBuilders = ( (IRegistryInfoGetter)  this.lookup).lookupForValueCopyViaBuilders().lookupOrThrow(resourceRegistryKey);
        return new PaperRegistryBuilderFactory<>(
        		resourceRegistryKey,
        		this,
        		buildableMeta.builderFiller(),
        		( (IRegistryWrapperImpl) ((HolderLookup.RegistryLookup)lookupForBuilders) )::getValueForCopying
        );
    }



}
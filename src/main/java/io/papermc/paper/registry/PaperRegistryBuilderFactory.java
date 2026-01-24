package io.papermc.paper.registry;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.registry.data.util.Conversions;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Registry;
import org.bukkit.Keyed;
import org.jspecify.annotations.Nullable;

public class PaperRegistryBuilderFactory<M, A extends Keyed, B extends PaperRegistryBuilder<M, A>> implements RegistryBuilderFactory<A, B> { // TODO remove Keyed

	private final net.minecraft.resources.ResourceKey<? extends Registry<M>> registryKey;
	
    private final Conversions conversions;
    private final PaperRegistryBuilder.Filler<M, A, B> builderFiller;
    // private final Function<? super Identifier, ? extends @Nullable M> existingValueGetter;
    private final Function<net.minecraft.resources.ResourceKey<M>, Optional<M>> existingValueGetter;
    
    private @Nullable B builder;

    /*
    public PaperRegistryBuilderFactory(final Conversions conversions, final PaperRegistryBuilder.Filler<M, A, B> builderFiller, final Function<? super Identifier, ? extends @Nullable M> existingValueGetter) {
        this.conversions = conversions;
        this.builderFiller = builderFiller;
        this.existingValueGetter = existingValueGetter;
    }
    */
    
    public PaperRegistryBuilderFactory(net.minecraft.resources.ResourceKey<? extends Registry<M>> registryKey, Conversions conversions, PaperRegistryBuilder.Filler<M, A, B> builderFiller, Function<net.minecraft.resources.ResourceKey<M>, Optional<M>> existingValueGetter) {
        this.registryKey = registryKey;
        this.conversions = conversions;
        this.builderFiller = builderFiller;
        this.existingValueGetter = existingValueGetter;
    }

    private void validate() {
        if (this.builder != null) {
            throw new IllegalStateException("Already created a builder");
        }
    }

    public B requireBuilder() {
        if (this.builder == null) {
            throw new IllegalStateException("Builder not created yet");
        }
        return this.builder;
    }

    @Override
    public B empty() {
        this.validate();
        return this.builder = this.builderFiller.create(this.conversions);
    }

    @Override
    /*
    public B copyFrom(final TypedKey<A> key) {
        this.validate();
        final M existing = this.existingValueGetter.apply(PaperAdventure.asVanilla(key));
        if (existing == null) {
            throw new IllegalArgumentException("Key " + key + " doesn't exist");
        }
        return this.builder = this.builderFiller.fill(this.conversions, existing);
    }
    */
    
    public B copyFrom(TypedKey<A> key) {
        this.validate();
        Optional<M> existing = this.existingValueGetter.apply(PaperAdventure.asVanilla(this.registryKey, key));
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Key " + String.valueOf(key) + " doesn't exist");
        }
        this.builder = this.builderFiller.fill(this.conversions, existing.get());
        return this.builder;
    }
}

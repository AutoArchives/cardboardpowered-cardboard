package io.papermc.paper.registry;

import io.papermc.paper.registry.RegistryBuilder;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.util.Conversions;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface PaperRegistryBuilder<M, T> extends RegistryBuilder<T> {

    public M build();

    @FunctionalInterface
    public static interface Factory<M, T, B extends PaperRegistryBuilder<M, T>> {
        public B create(Conversions var1, TypedKey<T> var2);
    }

    @FunctionalInterface
    public static interface Filler<M, T, B extends PaperRegistryBuilder<M, T>> {
        public B fill(Conversions var1, TypedKey<T> var2, @Nullable M var3);

        default public Factory<M, T, B> asFactory() {
            return (lookup, key) -> this.fill(lookup, key, null);
        }
    }

}
package io.papermc.paper.registry;

import io.papermc.paper.registry.RegistryBuilder;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.util.Conversions;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface PaperRegistryBuilder<M, T> extends RegistryBuilder<T> {

    M build();

    @FunctionalInterface
    interface Filler<M, T, B extends PaperRegistryBuilder<M, T>> {

        B fill(Conversions conversions, @Nullable M nms);

        default B create(final Conversions conversions) {
            return this.fill(conversions, null);
        }
    }

    @FunctionalInterface
    interface Factory<M, T, B extends PaperRegistryBuilder<M, T>> {

        B create(Conversions conversions);
    }
}
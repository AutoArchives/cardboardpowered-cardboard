package io.papermc.paper.registry.legacy;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public final class DelayedRegistry<T extends Keyed, R extends Registry<T>>
implements Registry<T> {
    private @MonotonicNonNull Supplier<? extends R> delegate;

    public void load(Supplier<? extends R> registry) {
        if (this.delegate != null) {
            throw new IllegalStateException("Registry already loaded!");
        }
        this.delegate = registry;
    }

    public Registry<T> delegate() {
        if (this.delegate == null) {
            throw new IllegalStateException("You are trying to access this registry too early!");
        }
        return this.delegate.get();
    }

    public @Nullable T get(NamespacedKey key) {
        return (T)this.delegate().get(key);
    }

    public Iterator<T> iterator() {
        return this.delegate().iterator();
    }

    public Stream<T> stream() {
        return this.delegate().stream();
    }

    public NamespacedKey getKey(T value) {
        return this.delegate().getKey(value);
    }

	@Override
	public @NotNull T getOrThrow(@NotNull NamespacedKey key) {
		return this.delegate().getOrThrow(key);
	}
}


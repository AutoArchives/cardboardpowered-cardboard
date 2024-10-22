package io.papermc.paper.registry.set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.papermc.paper.registry.PaperRegistries;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import java.util.Collection;
import java.util.Set;
import net.kyori.adventure.key.Key;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@com.github.bsideup.jabel.Desugar
@DefaultQualifier(value=NonNull.class)
public record NamedRegistryKeySetImpl<T extends Keyed, M>(TagKey<T> tagKey, RegistryEntryList.Named<M> namedSet) implements Tag<T>,
org.bukkit.Tag<T>
{
    public @Unmodifiable Collection<TypedKey<T>> values() {
        ImmutableList.Builder builder = ImmutableList.builder();
        for (RegistryEntry registryEntry : this.namedSet) {
            builder.add((Object)TypedKey.create((RegistryKey)this.tagKey.registryKey(), (Key)CraftNamespacedKey.fromMinecraft(((RegistryEntry.Reference)registryEntry).registryKey().getValue())));
        }
        return builder.build();
    }

    public RegistryKey<T> registryKey() {
        return this.tagKey.registryKey();
    }

    public boolean contains(TypedKey<T> valueKey) {
        return Iterables.any(this.namedSet, h2 -> PaperRegistries.fromNms(((RegistryEntry.Reference)h2).registryKey()).equals((Object)valueKey));
    }

    public @Unmodifiable Collection<T> resolve(Registry<T> registry) {
        ImmutableList.Builder builder = ImmutableList.builder();
        for (RegistryEntry registryEntry : this.namedSet) {
            builder.add((Object)registry.getOrThrow((Key)CraftNamespacedKey.fromMinecraft(((RegistryEntry.Reference)registryEntry).registryKey().getValue())));
        }
        return builder.build();
    }

    public boolean isTagged(T item) {
        return this.getValues().contains(item);
    }

    public Set<T> getValues() {
        return Set.copyOf(this.resolve(RegistryAccess.registryAccess().getRegistry(this.registryKey())));
    }

    @NotNull
    public NamespacedKey getKey() {
        Key key = this.tagKey().key();
        return new NamespacedKey(key.namespace(), key.value());
    }
}


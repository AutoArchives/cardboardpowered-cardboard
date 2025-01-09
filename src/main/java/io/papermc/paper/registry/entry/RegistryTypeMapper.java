package io.papermc.paper.registry.entry;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Either;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.registry.entry.RegistryEntry;
import org.bukkit.NamespacedKey;

public final class RegistryTypeMapper<M, A> {

    final Either<BiFunction<? super NamespacedKey, M, ? extends A>, Function<RegistryEntry<M>, ? extends A>> minecraftToBukkit;

    public RegistryTypeMapper(final BiFunction<? super NamespacedKey, M, ? extends A> byValueCreator) {
        this.minecraftToBukkit = Either.left(byValueCreator);
    }

    public RegistryTypeMapper(final Function<RegistryEntry<M>, ? extends A> byHolderCreator) {
        this.minecraftToBukkit = Either.right(byHolderCreator);
    }

    public A createBukkit(final NamespacedKey key, final RegistryEntry<M> minecraft) {
        return this.minecraftToBukkit.map(
            minecraftToBukkit -> minecraftToBukkit.apply(key, minecraft.value()),
            minecraftToBukkit -> minecraftToBukkit.apply(minecraft)
        );
    }

    public boolean supportsDirectHolders() {
        return this.minecraftToBukkit.right().isPresent();
    }

    public A convertDirectHolder(final RegistryEntry<M> directHolder) {
        Preconditions.checkArgument(this.supportsDirectHolders() && directHolder.getType() == RegistryEntry.Type.DIRECT);
        return this.minecraftToBukkit.right().orElseThrow().apply(directHolder);
    }
}

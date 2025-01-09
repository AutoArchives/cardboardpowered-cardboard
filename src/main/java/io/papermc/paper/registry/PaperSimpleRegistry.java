package io.papermc.paper.registry;

import io.papermc.paper.registry.set.NamedRegistryKeySetImpl;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import java.util.function.Predicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import org.bukkit.Keyed;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperSimpleRegistry<T extends Enum<T> & Keyed, M> extends Registry.SimpleRegistry<T> {

    static Registry<EntityType> entityType() {
        return new PaperSimpleRegistry<>(EntityType.class, entity -> entity != EntityType.UNKNOWN, Registries.ENTITY_TYPE);
    }

    static Registry<Particle> particleType() {
        return new PaperSimpleRegistry<>(Particle.class, Registries.PARTICLE_TYPE);
    }

    static Registry<PotionType> potion() {
        return new PaperSimpleRegistry<>(PotionType.class, Registries.POTION);
    }

    private final net.minecraft.registry.Registry<M> nmsRegistry;

    protected PaperSimpleRegistry(final Class<T> type, final net.minecraft.registry.Registry<M> nmsRegistry) {
        super(type);
        this.nmsRegistry = nmsRegistry;
    }

    public PaperSimpleRegistry(final Class<T> type, final Predicate<T> predicate, final net.minecraft.registry.Registry<M> nmsRegistry) {
        super(type, predicate);
        this.nmsRegistry = nmsRegistry;
    }

    @Override
    public boolean hasTag(final TagKey<T> key) {
        final net.minecraft.registry.tag.TagKey<M> nmsKey = PaperRegistries.toNms(key);
        return this.nmsRegistry.getOptional(nmsKey).isPresent();
    }

    @Override
    public Tag<T> getTag(final TagKey<T> key) {
        final RegistryEntryList.Named<M> namedHolderSet = this.nmsRegistry.getOptional(PaperRegistries.toNms(key)).orElseThrow();
        return new NamedRegistryKeySetImpl<>(key, namedHolderSet);
    }
}

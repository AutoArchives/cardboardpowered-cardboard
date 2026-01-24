package io.papermc.paper.registry.set;

import io.papermc.paper.registry.PaperRegistries;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.NamedRegistryKeySetImpl;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import java.util.ArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import org.bukkit.Keyed;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(value=NonNull.class)
public final class PaperRegistrySets {
    public static <A extends Keyed, M> HolderSet<M> convertToNms(net.minecraft.resources.ResourceKey<? extends Registry<M>> resourceKey, RegistryOps.RegistryInfoLookup lookup, RegistryKeySet<A> registryKeySet) {
        if (registryKeySet instanceof NamedRegistryKeySetImpl) {
            return ((NamedRegistryKeySetImpl)registryKeySet).namedSet();
        }
        RegistryOps.RegistryInfo registryInfo = lookup.lookup(resourceKey).orElseThrow();
        return HolderSet.direct(key -> registryInfo.getter().getOrThrow(PaperRegistries.toNms(key)), registryKeySet.values());
    }

    public static <A extends Keyed, M> RegistryKeySet<A> convertToApi(RegistryKey<A> registryKey, HolderSet<M> holders) {
        if (holders instanceof HolderSet.Named) {
            HolderSet.Named named = (HolderSet.Named)holders;
            return new NamedRegistryKeySetImpl(PaperRegistries.fromNms(named.key()), named);
        }
        ArrayList keys = new ArrayList();
        for (Holder registryEntry : holders) {
            if (!(registryEntry instanceof Holder.Reference)) {
                throw new UnsupportedOperationException("Cannot convert a holder set containing direct holders");
            }
            Holder.Reference reference = (Holder.Reference)registryEntry;
            keys.add(PaperRegistries.fromNms(reference.key()));
        }
        return RegistrySet.keySet(registryKey, keys);
    }

    private PaperRegistrySets() {
    }
}


package io.papermc.paper.registry.set;

import io.papermc.paper.registry.PaperRegistries;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.NamedRegistryKeySetImpl;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import java.util.ArrayList;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import org.bukkit.Keyed;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(value=NonNull.class)
public final class PaperRegistrySets {
    public static <A extends Keyed, M> RegistryEntryList<M> convertToNms(net.minecraft.registry.RegistryKey<? extends Registry<M>> resourceKey, RegistryOps.RegistryInfoGetter lookup, RegistryKeySet<A> registryKeySet) {
        if (registryKeySet instanceof NamedRegistryKeySetImpl) {
            return ((NamedRegistryKeySetImpl)registryKeySet).namedSet();
        }
        RegistryOps.RegistryInfo registryInfo = lookup.getRegistryInfo(resourceKey).orElseThrow();
        return RegistryEntryList.of(key -> registryInfo.entryLookup().getOrThrow(PaperRegistries.toNms(key)), registryKeySet.values());
    }

    public static <A extends Keyed, M> RegistryKeySet<A> convertToApi(RegistryKey<A> registryKey, RegistryEntryList<M> holders) {
        if (holders instanceof RegistryEntryList.Named) {
            RegistryEntryList.Named named = (RegistryEntryList.Named)holders;
            return new NamedRegistryKeySetImpl(PaperRegistries.fromNms(named.getTag()), named);
        }
        ArrayList keys = new ArrayList();
        for (RegistryEntry registryEntry : holders) {
            if (!(registryEntry instanceof RegistryEntry.Reference)) {
                throw new UnsupportedOperationException("Cannot convert a holder set containing direct holders");
            }
            RegistryEntry.Reference reference = (RegistryEntry.Reference)registryEntry;
            keys.add(PaperRegistries.fromNms(reference.registryKey()));
        }
        return RegistrySet.keySet(registryKey, keys);
    }

    private PaperRegistrySets() {
    }
}


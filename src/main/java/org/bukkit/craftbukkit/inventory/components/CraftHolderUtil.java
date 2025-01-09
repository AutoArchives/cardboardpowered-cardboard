package org.bukkit.craftbukkit.inventory.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

final class CraftHolderUtil {

    private CraftHolderUtil() {
    }

    public static void serialize(Map<String, Object> result, String key, RegistryEntryList<?> handle) {
        handle.getStorage()
                .ifLeft(tag -> result.put(key, "#" + tag.id().toString())) // Tag
                .ifRight(list -> result.put(key, list.stream().map((entry) -> entry.getKey().orElseThrow().getValue().toString()).toList())); // List
    }

    public static <T> RegistryEntryList<T> parse(Object parseObject, RegistryKey<Registry<T>> registryKey, Registry<T> registry) {
        RegistryEntryList<T> holderSet = null;

        if (parseObject instanceof String parseString && parseString.startsWith("#")) { // Tag
            parseString = parseString.substring(1);
            Identifier key = Identifier.tryParse(parseString);
            if (key != null) {
                holderSet = registry.getOptional(TagKey.of(registryKey, key)).orElse(null);
            }
        } else if (parseObject instanceof List parseList) { // List
            List<RegistryEntry.Reference<T>> holderList = new ArrayList<>(parseList.size());

            for (Object entry : parseList) {
                Identifier key = Identifier.tryParse(entry.toString());
                if (key == null) {
                    continue;
                }

                registry.getEntry(key).ifPresent(holderList::add);
            }

            holderSet = RegistryEntryList.of(holderList);
        } else {
            throw new IllegalArgumentException("(" + parseObject + ") is not a valid String or List");
        }

        if (holderSet == null) {
            holderSet = RegistryEntryList.empty();
        }

        return holderSet;
    }
}

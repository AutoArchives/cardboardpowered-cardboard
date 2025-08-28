package org.bukkit.craftbukkit.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.bukkit.craftbukkit.CraftRegistry;

public final class ConfigSerializationUtil {
    public static String getString(Map<?, ?> map, String key, boolean nullable) {
        return ConfigSerializationUtil.getObject(String.class, map, key, nullable);
    }

    public static UUID getUuid(Map<?, ?> map, String key, boolean nullable) {
        String uuidString = ConfigSerializationUtil.getString(map, key, nullable);
        if (uuidString == null) {
            return null;
        }
        return UUID.fromString(uuidString);
    }

    public static <T> T getObject(Class<T> clazz, Map<?, ?> map, String key, boolean nullable) {
        Object object = map.get(key);
        if (clazz.isInstance(object)) {
            return clazz.cast(object);
        }
        if (object == null) {
            if (!nullable) {
                throw new NoSuchElementException(String.valueOf(map) + " does not contain " + key);
            }
            return null;
        }
        throw new IllegalArgumentException(key + "(" + String.valueOf(object) + ") is not a valid " + String.valueOf(clazz));
    }

    public static void setHolderSet(Map<String, Object> result, String key, RegistryEntryList<?> holders) {
        holders.getStorage().ifLeft(tag -> result.put(key, "#" + tag.id().toString())).ifRight(list -> result.put(key, list.stream().map(entry -> entry.getKey().orElseThrow().getValue().toString()).toList()));
    }

    public static <T> RegistryEntryList<T> getHolderSet(Object from, RegistryKey<Registry<T>> registryKey) {
        String parseString;
        Registry<T> registry = CraftRegistry.getMinecraftRegistry(registryKey);
        if (from instanceof String && (parseString = (String)from).startsWith("#")) {
            Optional tag;
            Identifier key = Identifier.tryParse(parseString = parseString.substring(1));
            if (key != null && (tag = registry.getOptional(TagKey.of(registryKey, key))).isPresent()) {
                return (RegistryEntryList)tag.get();
            }
        } else {
            if (from instanceof List) {
                List parseList = (List)from;
                ArrayList holderList = new ArrayList(parseList.size());
                for (Object entry : parseList) {
                    Identifier key = Identifier.tryParse(entry.toString());
                    if (key == null) continue;
                    registry.getEntry(key).ifPresent(holderList::add);
                }
                return RegistryEntryList.of(holderList);
            }
            throw new IllegalArgumentException("(" + String.valueOf(from) + ") is not a valid String or List");
        }
        return RegistryEntryList.empty();
    }

    private ConfigSerializationUtil() {
    }
}


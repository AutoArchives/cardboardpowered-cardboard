package org.bukkit.craftbukkit.util;

import net.minecraft.util.Identifier;
import org.bukkit.NamespacedKey;

public final class CraftNamespacedKey {

    public CraftNamespacedKey() {
    }

    public static NamespacedKey fromStringOrNull(String string) {
        if (string == null || string.isEmpty()) return null;
        Identifier minecraft = Identifier.tryParse(string);
        return (minecraft == null) ? null : fromMinecraft(minecraft);
    }

    public static NamespacedKey fromString(String string) {
        return fromMinecraft(Identifier.of(string));
    }

    public static NamespacedKey fromMinecraft(Identifier minecraft) {
        return new NamespacedKey(minecraft.getNamespace(), minecraft.getPath());
    }

    public static Identifier toMinecraft(NamespacedKey key) {
        return Identifier.of(key.getNamespace(), key.getKey());
    }

}

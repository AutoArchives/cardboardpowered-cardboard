package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.Locale;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;

public class CraftEntityType {

    public static EntityType minecraftToBukkit(net.minecraft.entity.EntityType<?> minecraft) {
        Preconditions.checkArgument(minecraft != null);

        net.minecraft.registry.Registry<net.minecraft.entity.EntityType<?>> registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.ENTITY_TYPE);
        EntityType bukkit = Registry.ENTITY_TYPE.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft).orElseThrow().getValue()));

        Preconditions.checkArgument(bukkit != null);

        return bukkit;
    }

    private static final java.util.Map<EntityType, net.minecraft.registry.RegistryKey<net.minecraft.entity.EntityType<?>>> KEY_CACHE = java.util.Collections.synchronizedMap(new java.util.EnumMap<>(EntityType.class)); // Paper
    public static net.minecraft.entity.EntityType<?> bukkitToMinecraft(EntityType bukkit) {
        Preconditions.checkArgument(bukkit != null);
        return CraftRegistry.getMinecraftRegistry(RegistryKeys.ENTITY_TYPE)
                .getOptionalValue(KEY_CACHE.computeIfAbsent(bukkit, type -> net.minecraft.registry.RegistryKey.of(RegistryKeys.ENTITY_TYPE, CraftNamespacedKey.toMinecraft(type.getKey())))).orElseThrow();
    }

    public static RegistryEntry<net.minecraft.entity.EntityType<?>> bukkitToMinecraftHolder(EntityType bukkit) {
        Preconditions.checkArgument(bukkit != null);

        net.minecraft.registry.Registry<net.minecraft.entity.EntityType<?>> registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.ENTITY_TYPE);

        if (registry.getEntry(CraftEntityType.bukkitToMinecraft(bukkit)) instanceof RegistryEntry.Reference<net.minecraft.entity.EntityType<?>> holder) {
            return holder;
        }

        throw new IllegalArgumentException("No Reference holder found for " + bukkit
                + ", this can happen if a plugin creates its own sound effect with out properly registering it.");
    }

    public static String bukkitToString(EntityType bukkit) {
        Preconditions.checkArgument(bukkit != null);

        return bukkit.getKey().toString();
    }

    public static EntityType stringToBukkit(String string) {
        Preconditions.checkArgument(string != null);

        // We currently do not have any version-dependent remapping, so we can use current version
        // First convert from when only the names where saved
        string = FieldRename.convertEntityTypeName(ApiVersion.CURRENT, string);
        string = string.toLowerCase(Locale.ROOT);
        NamespacedKey key = NamespacedKey.fromString(string);

        // Now also convert from when keys where saved
        return CraftRegistry.get(Registry.ENTITY_TYPE, key, ApiVersion.CURRENT);
    }
}

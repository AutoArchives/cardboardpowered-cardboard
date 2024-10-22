package org.bukkit.craftbukkit.map;

import java.util.Locale;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.map.MapCursor;

public final class CraftMapCursor {

    public static final class CraftType
    implements MapCursor.Type,
    Handleable<MapDecorationType> {
        private static int count = 0;
        private final NamespacedKey key;
        private final MapDecorationType mapDecorationType;
        private final String name;
        private final int ordinal;

        public static MapCursor.Type minecraftToBukkit(MapDecorationType minecraft) {
            return (MapCursor.Type)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.MAP_DECORATION_TYPE, Registry.MAP_DECORATION_TYPE);
        }

        public static MapCursor.Type minecraftHolderToBukkit(RegistryEntry<MapDecorationType> minecraft) {
            return CraftType.minecraftToBukkit(minecraft.value());
        }

        public static MapDecorationType bukkitToMinecraft(MapCursor.Type bukkit) {
            return (MapDecorationType)CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public static RegistryEntry<MapDecorationType> bukkitToMinecraftHolder(MapCursor.Type bukkit) {
            return CraftRegistry.bukkitToMinecraftHolder(bukkit, RegistryKeys.MAP_DECORATION_TYPE);
        }

        public CraftType(NamespacedKey key, MapDecorationType mapDecorationType) {
            this.key = key;
            this.mapDecorationType = mapDecorationType;
            this.name = "minecraft".equals(key.getNamespace()) ? key.getKey().toUpperCase(Locale.ROOT) : key.toString();
            this.ordinal = count++;
        }

        @Override
        public MapDecorationType getHandle() {
            return this.mapDecorationType;
        }

        public NamespacedKey getKey() {
            return this.key;
        }

        public int compareTo(MapCursor.Type type) {
            return this.ordinal - type.ordinal();
        }

        public String name() {
            return this.name;
        }

        public int ordinal() {
            return this.ordinal;
        }

        public String toString() {
            return this.name();
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CraftType)) {
                return false;
            }
            return this.getKey().equals((Object)((MapCursor.Type)other).getKey());
        }

        public int hashCode() {
            return this.getKey().hashCode();
        }

        public byte getValue() {
            return (byte)CraftRegistry.getMinecraftRegistry(RegistryKeys.MAP_DECORATION_TYPE).getRawId(this.getHandle());
        }
    }
}
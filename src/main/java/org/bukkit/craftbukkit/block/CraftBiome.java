package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Locale;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.Handleable;

public class CraftBiome implements Biome, Handleable<net.minecraft.world.biome.Biome> {

	 private static int count = 0;
	
    public static Biome minecraftToBukkit(net.minecraft.world.biome.Biome minecraft) {
        Preconditions.checkArgument(minecraft != null);

        net.minecraft.registry.Registry<net.minecraft.world.biome.Biome> registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.BIOME);
        Biome bukkit = Registry.BIOME.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft).orElseThrow().getValue()));

        if (bukkit == null) {
            return Biome.CUSTOM;
        }

        return bukkit;
    }

    public static Biome minecraftHolderToBukkit(RegistryEntry<net.minecraft.world.biome.Biome> minecraft) {
        return CraftBiome.minecraftToBukkit(minecraft.value());
    }

    public static net.minecraft.world.biome.Biome bukkitToMinecraft(Biome bukkit) {
        if (bukkit == null || bukkit == Biome.CUSTOM) {
            return null;
        }

        return CraftRegistry.getMinecraftRegistry(RegistryKeys.BIOME)
                .getOptionalValue(CraftNamespacedKey.toMinecraft(bukkit.getKey())).orElseThrow();
    }

    public static RegistryEntry<net.minecraft.world.biome.Biome> bukkitToMinecraftHolder(Biome bukkit) {
        if (bukkit == null || bukkit == Biome.CUSTOM) {
            return null;
        }

        net.minecraft.registry.Registry<net.minecraft.world.biome.Biome> registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.BIOME);

        if (registry.getEntry(CraftBiome.bukkitToMinecraft(bukkit)) instanceof RegistryEntry.Reference<net.minecraft.world.biome.Biome> holder) {
            return holder;
        }

        throw new IllegalArgumentException("No Reference holder found for " + bukkit
                + ", this can happen if a plugin creates its own biome base with out properly registering it.");
    }
    
    private final NamespacedKey key;
    private final net.minecraft.world.biome.Biome biomeBase;
    private final String name;
    private final int ordinal;

    public CraftBiome(NamespacedKey key, net.minecraft.world.biome.Biome biomeBase) {
        this.key = key;
        this.biomeBase = biomeBase;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive biome specific values.
        // Custom biomes will return the key with namespace. For a plugin this should look than like a new biome
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase(Locale.ROOT);
        } else {
            this.name = key.toString();
        }
        this.ordinal = CraftBiome.count++;
    }

    @Override
    public net.minecraft.world.biome.Biome getHandle() {
        return this.biomeBase;
    }

    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    public int compareTo(Biome biome) {
        return this.ordinal - biome.ordinal();
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public int ordinal() {
        return this.ordinal;
    }

    @Override
    public String toString() {
        // For backwards compatibility
        return this.name();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CraftBiome otherBiome)) {
            return false;
        }

        return this.getKey().equals(otherBiome.getKey());
    }

    @Override
    public int hashCode() {
        return this.getKey().hashCode();
    }

}
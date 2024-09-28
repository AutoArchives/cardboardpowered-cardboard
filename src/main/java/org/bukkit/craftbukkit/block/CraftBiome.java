package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class CraftBiome {

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
                .getOrEmpty(CraftNamespacedKey.toMinecraft(bukkit.getKey())).orElseThrow();
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

}
package org.bukkit.craftbukkit.block.banner;

import com.google.common.base.Preconditions;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.jetbrains.annotations.Nullable;

public class CraftPatternType {

    public static PatternType minecraftToBukkit(BannerPattern minecraft) {
        Preconditions.checkArgument((minecraft != null ? 1 : 0) != 0);
        net.minecraft.registry.Registry<BannerPattern> registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.BANNER_PATTERN);
        
        PatternType bukkit = PatternType.getByIdentifier( registry.getKey(minecraft).orElseThrow().getValue().toString() );
        
        //PatternType bukkit = (PatternType)Registry.BANNER_PATTERN.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft).orElseThrow().getValue()));
        //Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        return bukkit;
    }

    public static PatternType minecraftHolderToBukkit(RegistryEntry<BannerPattern> minecraft) {
        return CraftPatternType.minecraftToBukkit(minecraft.value());
    }

    public static BannerPattern bukkitToMinecraft(PatternType bukkit) {
    	
    	// NamespacedKey key = bukkit.getKey();
    	
    	NamespacedKey key = NamespacedKey.fromString(bukkit.getIdentifier());
    	
        Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        return (BannerPattern)CraftRegistry.getMinecraftRegistry(RegistryKeys.BANNER_PATTERN).getOrEmpty(CraftNamespacedKey.toMinecraft(key)).orElseThrow();
    }

    public static RegistryEntry<BannerPattern> bukkitToMinecraftHolder(PatternType bukkit) {
        Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        net.minecraft.registry.Registry registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.BANNER_PATTERN);
        RegistryEntry<BannerPattern> registryEntry = registry.getEntry(CraftPatternType.bukkitToMinecraft(bukkit));
        if (registryEntry instanceof RegistryEntry.Reference) {
            RegistryEntry.Reference holder = (RegistryEntry.Reference)registryEntry;
            return holder;
        }
        throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own banner pattern without properly registering it.");
    }

}
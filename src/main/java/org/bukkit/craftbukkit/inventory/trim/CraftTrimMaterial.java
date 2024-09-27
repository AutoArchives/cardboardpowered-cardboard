package org.bukkit.craftbukkit.inventory.trim;

import com.google.common.base.Preconditions;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.TranslatableTextContent;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.jetbrains.annotations.NotNull;

public class CraftTrimMaterial implements TrimMaterial, Handleable<ArmorTrimMaterial> {
	
    private final NamespacedKey key;
    private final ArmorTrimMaterial handle;

    public static TrimMaterial minecraftToBukkit(ArmorTrimMaterial minecraft) {
        return (TrimMaterial)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.TRIM_MATERIAL, Registry.TRIM_MATERIAL);
    }

    public static TrimMaterial minecraftHolderToBukkit(RegistryEntry<ArmorTrimMaterial> minecraft) {
        return CraftTrimMaterial.minecraftToBukkit(minecraft.value());
    }

    public static ArmorTrimMaterial bukkitToMinecraft(TrimMaterial bukkit) {
        return (ArmorTrimMaterial)CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static RegistryEntry<ArmorTrimMaterial> bukkitToMinecraftHolder(TrimMaterial bukkit) {
        Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        net.minecraft.registry.Registry registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.TRIM_MATERIAL);
        RegistryEntry<ArmorTrimMaterial> registryEntry = registry.getEntry(CraftTrimMaterial.bukkitToMinecraft(bukkit));
        if (registryEntry instanceof RegistryEntry.Reference) {
            RegistryEntry.Reference holder = (RegistryEntry.Reference)registryEntry;
            return holder;
        }
        throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own trim material without properly registering it.");
    }

    public CraftTrimMaterial(NamespacedKey key, ArmorTrimMaterial handle) {
        this.key = key;
        this.handle = handle;
    }

    @Override
    public ArmorTrimMaterial getHandle() {
        return this.handle;
    }

    @NotNull
    public NamespacedKey getKey() {
        return key;
    	// return Objects.requireNonNull(Registry.TRIM_MATERIAL.get((Keyed)this), () -> String.valueOf(this) + " doesn't have a key");
    }

    @NotNull
    public String getTranslationKey() {
        if (!(this.handle.description().getContent() instanceof TranslatableTextContent)) {
            throw new UnsupportedOperationException("Description isn't translatable!");
        }
        return ((TranslatableTextContent)this.handle.description().getContent()).getKey();
    }

    public Component description() {
    	return CardboardAdventure.asAdventure(this.handle.description());
    }

}


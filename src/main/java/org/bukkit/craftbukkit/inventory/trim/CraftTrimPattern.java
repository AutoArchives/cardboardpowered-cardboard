package org.bukkit.craftbukkit.inventory.trim;

import com.google.common.base.Preconditions;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.TranslatableTextContent;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.jetbrains.annotations.NotNull;

public class CraftTrimPattern implements TrimPattern, Handleable<ArmorTrimPattern> {

    private final NamespacedKey key;
    private final ArmorTrimPattern handle;

    public static TrimPattern minecraftToBukkit(ArmorTrimPattern minecraft) {
        return (TrimPattern)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.TRIM_PATTERN, Registry.TRIM_PATTERN);
    }

    public static TrimPattern minecraftHolderToBukkit(RegistryEntry<ArmorTrimPattern> minecraft) {
        return CraftTrimPattern.minecraftToBukkit(minecraft.value());
    }

    public static ArmorTrimPattern bukkitToMinecraft(TrimPattern bukkit) {
        return (ArmorTrimPattern)CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static RegistryEntry<ArmorTrimPattern> bukkitToMinecraftHolder(TrimPattern bukkit) {
        Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        net.minecraft.registry.Registry registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.TRIM_PATTERN);
        RegistryEntry<ArmorTrimPattern> registryEntry = registry.getEntry(CraftTrimPattern.bukkitToMinecraft(bukkit));
        if (registryEntry instanceof RegistryEntry.Reference) {
            RegistryEntry.Reference holder = (RegistryEntry.Reference)registryEntry;
            return holder;
        }
        throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own trim pattern without properly registering it.");
    }

    public CraftTrimPattern(NamespacedKey key, ArmorTrimPattern handle) {
        this.key = key;
        this.handle = handle;
    }

    @Override
    public ArmorTrimPattern getHandle() {
        return this.handle;
    }

    @NotNull
    public NamespacedKey getKey() {
        return key;
    	// return Objects.requireNonNull(Registry.TRIM_PATTERN.getKey((Keyed)this), () -> String.valueOf(this) + " doesn't have a key");
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


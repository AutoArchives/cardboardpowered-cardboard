package org.bukkit.craftbukkit.inventory.trim;

import com.google.common.base.Preconditions;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.jetbrains.annotations.NotNull;

public class CraftTrimPattern implements TrimPattern, Handleable<net.minecraft.world.item.equipment.trim.TrimPattern> {

    private final NamespacedKey key;
    private final net.minecraft.world.item.equipment.trim.TrimPattern handle;

    public static TrimPattern minecraftToBukkit(net.minecraft.world.item.equipment.trim.TrimPattern minecraft) {
        return (TrimPattern)CraftRegistry.minecraftToBukkit(minecraft, Registries.TRIM_PATTERN);
    }

    public static TrimPattern minecraftHolderToBukkit(Holder<net.minecraft.world.item.equipment.trim.TrimPattern> minecraft) {
        return CraftTrimPattern.minecraftToBukkit(minecraft.value());
    }

    public static net.minecraft.world.item.equipment.trim.TrimPattern bukkitToMinecraft(TrimPattern bukkit) {
        return (net.minecraft.world.item.equipment.trim.TrimPattern)CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static Holder<net.minecraft.world.item.equipment.trim.TrimPattern> bukkitToMinecraftHolder(TrimPattern bukkit) {
        Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        net.minecraft.core.Registry registry = CraftRegistry.getMinecraftRegistry(Registries.TRIM_PATTERN);
        Holder<net.minecraft.world.item.equipment.trim.TrimPattern> registryEntry = registry.wrapAsHolder(CraftTrimPattern.bukkitToMinecraft(bukkit));
        if (registryEntry instanceof Holder.Reference) {
            Holder.Reference holder = (Holder.Reference)registryEntry;
            return holder;
        }
        throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own trim pattern without properly registering it.");
    }

    public CraftTrimPattern(NamespacedKey key, net.minecraft.world.item.equipment.trim.TrimPattern handle) {
        this.key = key;
        this.handle = handle;
    }

    @Override
    public net.minecraft.world.item.equipment.trim.TrimPattern getHandle() {
        return this.handle;
    }

    @NotNull
    public NamespacedKey getKey() {
        return key;
    	// return Objects.requireNonNull(Registry.TRIM_PATTERN.getKey((Keyed)this), () -> String.valueOf(this) + " doesn't have a key");
    }

    @NotNull
    public String getTranslationKey() {
        if (!(this.handle.description().getContents() instanceof TranslatableContents)) {
            throw new UnsupportedOperationException("Description isn't translatable!");
        }
        return ((TranslatableContents)this.handle.description().getContents()).getKey();
    }

    public Component description() {
        return CardboardAdventure.asAdventure(this.handle.description());
    }
}


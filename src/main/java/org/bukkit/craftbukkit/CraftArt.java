package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import java.util.Locale;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import org.bukkit.Art;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.Handleable;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.jetbrains.annotations.NotNull;

public class CraftArt
implements Art,
Handleable<PaintingVariant> {
    private static int count = 0;
    private final NamespacedKey key;
    private final PaintingVariant paintingVariant;
    private final String name;
    private final int ordinal;

    public static Art minecraftToBukkit(PaintingVariant minecraft) {
        return (Art)CraftRegistry.minecraftToBukkit(minecraft, Registries.PAINTING_VARIANT);
    }

    public static Art minecraftHolderToBukkit(Holder<PaintingVariant> minecraft) {
        return CraftArt.minecraftToBukkit(minecraft.value());
    }

    public static PaintingVariant bukkitToMinecraft(Art bukkit) {
        return (PaintingVariant)CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static Holder<PaintingVariant> bukkitToMinecraftHolder(Art bukkit) {
        Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        net.minecraft.core.Registry registry = CraftRegistry.getMinecraftRegistry(Registries.PAINTING_VARIANT);
        Holder<PaintingVariant> registryEntry = registry.wrapAsHolder(CraftArt.bukkitToMinecraft(bukkit));
        if (registryEntry instanceof Holder.Reference) {
            Holder.Reference holder = (Holder.Reference)registryEntry;
            return holder;
        }
        throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own painting variant with out properly registering it.");
    }

    public CraftArt(NamespacedKey key, PaintingVariant paintingVariant) {
        this.key = key;
        this.paintingVariant = paintingVariant;
        this.name = "minecraft".equals(key.getNamespace()) ? key.getKey().toUpperCase(Locale.ROOT) : key.toString();
        this.ordinal = count++;
    }

    @Override
    public PaintingVariant getHandle() {
        return this.paintingVariant;
    }

    public int getBlockWidth() {
        return this.paintingVariant.width();
    }

    public int getBlockHeight() {
        return this.paintingVariant.height();
    }

    public Component title() {
        return this.paintingVariant.title().map(CardboardAdventure::asAdventure).orElse(null);
    }

    public Component author() {
        return this.paintingVariant.author().map(CardboardAdventure::asAdventure).orElse(null);
    }

    public Key assetId() {
        return CardboardAdventure.asAdventure(this.paintingVariant.assetId());
    }

    public int getId() {
        return CraftRegistry.getMinecraftRegistry(Registries.PAINTING_VARIANT).getId(this.paintingVariant);
    }

    @NotNull
    public NamespacedKey getKey() {
        return Objects.requireNonNull(Registry.ART.getKey(this), () -> String.valueOf(this) + " doesn't have a key");
    }

    public int compareTo(@NotNull Art art) {
        return this.ordinal - art.ordinal();
    }

    @NotNull
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
        if (!(other instanceof CraftArt)) {
            return false;
        }
        CraftArt otherArt = (CraftArt)other;
        return this.getKey().equals((Object)otherArt.getKey());
    }

    public int hashCode() {
        return this.getKey().hashCode();
    }
}


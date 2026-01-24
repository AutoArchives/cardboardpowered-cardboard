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
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.jetbrains.annotations.NotNull;

public class CraftTrimMaterial implements TrimMaterial, Handleable<net.minecraft.world.item.equipment.trim.TrimMaterial> {
	
    private final NamespacedKey key;
    private final net.minecraft.world.item.equipment.trim.TrimMaterial handle;

    public static TrimMaterial minecraftToBukkit(net.minecraft.world.item.equipment.trim.TrimMaterial minecraft) {
        return (TrimMaterial)CraftRegistry.minecraftToBukkit(minecraft, Registries.TRIM_MATERIAL);
    }

    public static TrimMaterial minecraftHolderToBukkit(Holder<net.minecraft.world.item.equipment.trim.TrimMaterial> minecraft) {
        return CraftTrimMaterial.minecraftToBukkit(minecraft.value());
    }

    public static net.minecraft.world.item.equipment.trim.TrimMaterial bukkitToMinecraft(TrimMaterial bukkit) {
        return (net.minecraft.world.item.equipment.trim.TrimMaterial)CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static Holder<net.minecraft.world.item.equipment.trim.TrimMaterial> bukkitToMinecraftHolder(TrimMaterial bukkit) {
        Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        net.minecraft.core.Registry registry = CraftRegistry.getMinecraftRegistry(Registries.TRIM_MATERIAL);
        Holder<net.minecraft.world.item.equipment.trim.TrimMaterial> registryEntry = registry.wrapAsHolder(CraftTrimMaterial.bukkitToMinecraft(bukkit));
        if (registryEntry instanceof Holder.Reference) {
            Holder.Reference holder = (Holder.Reference)registryEntry;
            return holder;
        }
        throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own trim material without properly registering it.");
    }

    public CraftTrimMaterial(NamespacedKey key, net.minecraft.world.item.equipment.trim.TrimMaterial handle) {
        this.key = key;
        this.handle = handle;
    }

    @Override
    public net.minecraft.world.item.equipment.trim.TrimMaterial getHandle() {
        return this.handle;
    }

    @NotNull
    public NamespacedKey getKey() {
        return key;
    	// return Objects.requireNonNull(Registry.TRIM_MATERIAL.get((Keyed)this), () -> String.valueOf(this) + " doesn't have a key");
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


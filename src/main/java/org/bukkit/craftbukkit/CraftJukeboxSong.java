package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.TranslatableTextContent;
import org.bukkit.JukeboxSong;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.NotNull;

public class CraftJukeboxSong implements JukeboxSong, Handleable<net.minecraft.block.jukebox.JukeboxSong> {

    private final NamespacedKey key;
    private final net.minecraft.block.jukebox.JukeboxSong handle;

    public static JukeboxSong minecraftToBukkit(net.minecraft.block.jukebox.JukeboxSong minecraft) {
        return (JukeboxSong)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.JUKEBOX_SONG, Registry.JUKEBOX_SONG);
    }

    public static JukeboxSong minecraftHolderToBukkit(RegistryEntry<net.minecraft.block.jukebox.JukeboxSong> minecraft) {
        return CraftJukeboxSong.minecraftToBukkit(minecraft.value());
    }

    public static net.minecraft.block.jukebox.JukeboxSong bukkitToMinecraft(JukeboxSong bukkit) {
        return (net.minecraft.block.jukebox.JukeboxSong)CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static RegistryEntry<net.minecraft.block.jukebox.JukeboxSong> bukkitToMinecraftHolder(JukeboxSong bukkit) {
        Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        net.minecraft.registry.Registry registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.JUKEBOX_SONG);
        RegistryEntry<net.minecraft.block.jukebox.JukeboxSong> registryEntry = registry.getEntry(CraftJukeboxSong.bukkitToMinecraft(bukkit));
        if (registryEntry instanceof RegistryEntry.Reference) {
            RegistryEntry.Reference holder = (RegistryEntry.Reference)registryEntry;
            return holder;
        }
        throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own trim pattern without properly registering it.");
    }

    public CraftJukeboxSong(NamespacedKey key, net.minecraft.block.jukebox.JukeboxSong handle) {
        this.key = key;
        this.handle = handle;
    }

    @Override
    public net.minecraft.block.jukebox.JukeboxSong getHandle() {
        return this.handle;
    }

    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @NotNull
    public String getTranslationKey() {
        if (!(this.handle.description().getContent() instanceof TranslatableTextContent)) {
            throw new UnsupportedOperationException("Description isn't translatable!");
        }
        return ((TranslatableTextContent)this.handle.description().getContent()).getKey();
    }

}

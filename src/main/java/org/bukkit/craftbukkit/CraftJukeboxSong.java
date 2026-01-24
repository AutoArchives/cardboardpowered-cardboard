package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;

import net.kyori.adventure.text.Component;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.bukkit.JukeboxSong;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.NotNull;

public class CraftJukeboxSong implements JukeboxSong, Handleable<net.minecraft.world.item.JukeboxSong> {

    private final NamespacedKey key;
    private final net.minecraft.world.item.JukeboxSong handle;

    public static JukeboxSong minecraftToBukkit(net.minecraft.world.item.JukeboxSong minecraft) {
        return (JukeboxSong)CraftRegistry.minecraftToBukkit(minecraft, Registries.JUKEBOX_SONG);
    }

    public static JukeboxSong minecraftHolderToBukkit(Holder<net.minecraft.world.item.JukeboxSong> minecraft) {
        return CraftJukeboxSong.minecraftToBukkit(minecraft.value());
    }

    public static net.minecraft.world.item.JukeboxSong bukkitToMinecraft(JukeboxSong bukkit) {
        return (net.minecraft.world.item.JukeboxSong)CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static Holder<net.minecraft.world.item.JukeboxSong> bukkitToMinecraftHolder(JukeboxSong bukkit) {
        Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        net.minecraft.core.Registry registry = CraftRegistry.getMinecraftRegistry(Registries.JUKEBOX_SONG);
        Holder<net.minecraft.world.item.JukeboxSong> registryEntry = registry.wrapAsHolder(CraftJukeboxSong.bukkitToMinecraft(bukkit));
        if (registryEntry instanceof Holder.Reference) {
            Holder.Reference holder = (Holder.Reference)registryEntry;
            return holder;
        }
        throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own trim pattern without properly registering it.");
    }

    public CraftJukeboxSong(NamespacedKey key, net.minecraft.world.item.JukeboxSong handle) {
        this.key = key;
        this.handle = handle;
    }

    @Override
    public net.minecraft.world.item.JukeboxSong getHandle() {
        return this.handle;
    }

    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @NotNull
    public String getTranslationKey() {
        if (!(this.handle.description().getContents() instanceof TranslatableContents)) {
            throw new UnsupportedOperationException("Description isn't translatable!");
        }
        return ((TranslatableContents)this.handle.description().getContents()).getKey();
    }
    
    // TODO: Update this:

	@Override
	public int getComparatorOutput() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Component getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getLengthInSeconds() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Sound getSound() {
		// TODO Auto-generated method stub
		return null;
	}

}

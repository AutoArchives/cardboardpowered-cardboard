package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.item.Instrument;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.NotNull;

public class CraftMusicInstrument extends MusicInstrument implements Handleable<Instrument> {

    public static MusicInstrument minecraftToBukkit(Instrument minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.INSTRUMENT);
    }

    public static MusicInstrument minecraftHolderToBukkit(RegistryEntry<Instrument> minecraft) {
        return CraftMusicInstrument.minecraftToBukkit(minecraft.value());
    }

    public static Instrument bukkitToMinecraft(MusicInstrument bukkit) {
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static RegistryEntry<Instrument> bukkitToMinecraftHolder(MusicInstrument bukkit) {
        Preconditions.checkArgument(bukkit != null);

        net.minecraft.registry.Registry<Instrument> registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.INSTRUMENT);

        if (registry.getEntry(CraftMusicInstrument.bukkitToMinecraft(bukkit)) instanceof RegistryEntry.Reference<Instrument> holder) {
            return holder;
        }

        throw new IllegalArgumentException("No Reference holder found for " + bukkit
                + ", this can happen if a plugin creates its own instrument without properly registering it.");
    }

    private final NamespacedKey key;
    private final Instrument handle;

    public CraftMusicInstrument(NamespacedKey key, Instrument handle) {
        this.key = key;
        this.handle = handle;
    }

    @Override
    public Instrument getHandle() {
        return this.handle;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CraftMusicInstrument)) {
            return false;
        }

        return this.getKey().equals(((MusicInstrument) other).getKey());
    }

    @Override
    public int hashCode() {
        return this.getKey().hashCode();
    }

    @Override
    public String toString() {
        return "CraftMusicInstrument{key=" + this.key + "}";
    }

    @Override
    public @NotNull String translationKey() {
        if (!(this.getHandle().description().getContent() instanceof final net.minecraft.text.TranslatableTextContent translatableContents)) {
            throw new UnsupportedOperationException("Description isn't translatable!"); // Paper
        }
        return translatableContents.getKey();
    }

	@Override
	public float getDuration() {
		return ((Instrument)this.getHandle()).useDuration();
	}

	@Override
	public float getRange() {
		return ((Instrument)this.getHandle()).range();
	}

	@Override
	public Component description() {
		return PaperAdventure.asAdventure(((Instrument)this.getHandle()).description());
	}

	@Override
	public Sound getSound() {
		return CraftSound.MUSIC_DISC_11;
		// return CraftSound.minecraftHolderToBukkit(((Instrument)this.getHandle()).soundEvent());
	}

}

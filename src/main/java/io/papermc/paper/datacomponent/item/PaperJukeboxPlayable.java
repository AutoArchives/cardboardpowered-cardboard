package io.papermc.paper.datacomponent.item;

import org.bukkit.JukeboxSong;
import org.bukkit.craftbukkit.CraftJukeboxSong;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.Handleable;

import net.minecraft.registry.entry.LazyRegistryEntryReference;

public record PaperJukeboxPlayable(
    net.minecraft.component.type.JukeboxPlayableComponent impl
) implements JukeboxPlayable, Handleable<net.minecraft.component.type.JukeboxPlayableComponent> {

    @Override
    public net.minecraft.component.type.JukeboxPlayableComponent getHandle() {
        return this.impl;
    }

    @Override
    public JukeboxSong jukeboxSong() {
        return this.impl.song()
            .resolveEntry(CraftRegistry.getMinecraftRegistry())
            .map(CraftJukeboxSong::minecraftHolderToBukkit)
            .orElseThrow();
    }

    static final class BuilderImpl implements JukeboxPlayable.Builder {

        private JukeboxSong song;

        BuilderImpl(final JukeboxSong song) {
            this.song = song;
        }

        @Override
        public JukeboxPlayable.Builder jukeboxSong(final JukeboxSong song) {
            this.song = song;
            return this;
        }

        @Override
        public JukeboxPlayable build() {
            return new PaperJukeboxPlayable(new net.minecraft.component.type.JukeboxPlayableComponent(new LazyRegistryEntryReference<>(CraftJukeboxSong.bukkitToMinecraftHolder(this.song))));
        }
    }
}

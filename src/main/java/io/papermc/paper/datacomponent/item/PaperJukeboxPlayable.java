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

    // @Override
    public boolean showInTooltip() {
        return false; // 1.21.8: removed
    	// return this.impl.showInTooltip();
    }

    @Override
    public PaperJukeboxPlayable showInTooltip(final boolean showInTooltip) {
        // return new PaperJukeboxPlayable(this.impl.withShowInTooltip(showInTooltip));
        
        // TODO: 1.21.8: removed
        
        return new PaperJukeboxPlayable(this.impl);
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
        private boolean showInTooltip = true;

        BuilderImpl(final JukeboxSong song) {
            this.song = song;
        }

        @Override
        public JukeboxPlayable.Builder showInTooltip(final boolean showInTooltip) {
            this.showInTooltip = showInTooltip;
            return this;
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

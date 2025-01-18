package io.papermc.paper.datacomponent.item;

import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.Handleable;
import org.jspecify.annotations.Nullable;

public record PaperLodestoneTracker(
    net.minecraft.component.type.LodestoneTrackerComponent impl
) implements LodestoneTracker, Handleable<net.minecraft.component.type.LodestoneTrackerComponent> {

    @Override
    public net.minecraft.component.type.LodestoneTrackerComponent getHandle() {
        return this.impl;
    }

    @Override
    public @Nullable Location location() {
        return this.impl.target().map(CraftLocation::fromGlobalPos).orElse(null);
    }

    @Override
    public boolean tracked() {
        return this.impl.tracked();
    }

    static final class BuilderImpl implements LodestoneTracker.Builder {

        private @Nullable Location location;
        private boolean tracked = true;

        @Override
        public LodestoneTracker.Builder location(final @Nullable Location location) {
            this.location = location;
            return this;
        }

        @Override
        public LodestoneTracker.Builder tracked(final boolean tracked) {
            this.tracked = tracked;
            return this;
        }

        @Override
        public LodestoneTracker build() {
            return new PaperLodestoneTracker(new net.minecraft.component.type.LodestoneTrackerComponent(
                Optional.ofNullable(this.location).map(CraftLocation::toGlobalPos),
                this.tracked
            ));
        }
    }
}

package io.papermc.paper.registry.data;

import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.util.Checks;
import io.papermc.paper.registry.data.util.Conversions;
import java.util.OptionalInt;
import org.bukkit.GameEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.Range;

@DefaultQualifier(value=NonNull.class)
public class PaperGameEventRegistryEntry
implements GameEventRegistryEntry {
    protected OptionalInt range = OptionalInt.empty();

    public PaperGameEventRegistryEntry(Conversions ignoredConversions, TypedKey<GameEvent> ignoredKey, net.minecraft.world.event.GameEvent nms) {
        if (nms == null) {
            return;
        }
        this.range = OptionalInt.of(nms.notificationRadius());
    }

    public @Range(from=0L, to=0x7FFFFFFFL) int range() {
        return Checks.asConfigured(this.range, "range");
    }

    public static final class PaperBuilder extends PaperGameEventRegistryEntry
    implements GameEventRegistryEntry.Builder, PaperRegistryBuilder<net.minecraft.world.event.GameEvent, GameEvent> {
        public PaperBuilder(Conversions conversions, TypedKey<GameEvent> key, net.minecraft.world.event.GameEvent nms) {
            super(conversions, key, nms);
        }

        public GameEventRegistryEntry.Builder range(@Range(from=0L, to=0x7FFFFFFFL) int range) {
            this.range = OptionalInt.of(Checks.asArgumentMin(range, "range", 0));
            return this;
        }

        @Override
        public net.minecraft.world.event.GameEvent build() {
            return new net.minecraft.world.event.GameEvent(this.range());
        }
    }

}
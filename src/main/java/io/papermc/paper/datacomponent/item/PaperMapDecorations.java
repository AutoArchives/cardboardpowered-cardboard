package io.papermc.paper.datacomponent.item;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.bukkit.craftbukkit.map.CraftMapCursor;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.map.MapCursor;
import org.jspecify.annotations.Nullable;

public record PaperMapDecorations(
    net.minecraft.component.type.MapDecorationsComponent impl
) implements MapDecorations, Handleable<net.minecraft.component.type.MapDecorationsComponent> {

    @Override
    public net.minecraft.component.type.MapDecorationsComponent getHandle() {
        return this.impl;
    }

    @Override
    public @Nullable DecorationEntry decoration(final String id) {
        final net.minecraft.component.type.MapDecorationsComponent.Decoration decoration = this.impl.decorations().get(id);
        if (decoration == null) {
            return null;
        }

        return new PaperDecorationEntry(decoration);
    }

    @Override
    public Map<String, DecorationEntry> decorations() {
        if (this.impl.decorations().isEmpty()) {
            return Collections.emptyMap();
        }

        final Set<Map.Entry<String, net.minecraft.component.type.MapDecorationsComponent.Decoration>> entries = this.impl.decorations().entrySet();
        final Map<String, DecorationEntry> decorations = new Object2ObjectOpenHashMap<>(entries.size());
        for (final Map.Entry<String, net.minecraft.component.type.MapDecorationsComponent.Decoration> entry : entries) {
            decorations.put(entry.getKey(), new PaperDecorationEntry(entry.getValue()));
        }

        return Collections.unmodifiableMap(decorations);
    }

    public record PaperDecorationEntry(net.minecraft.component.type.MapDecorationsComponent.Decoration entry) implements DecorationEntry {

        public static DecorationEntry toApi(final MapCursor.Type type, final double x, final double z, final float rotation) {
            return new PaperDecorationEntry(new net.minecraft.component.type.MapDecorationsComponent.Decoration(CraftMapCursor.CraftType.bukkitToMinecraftHolder(type), x, z, rotation));
        }

        @Override
        public MapCursor.Type type() {
            return CraftMapCursor.CraftType.minecraftHolderToBukkit(this.entry.type());
        }

        @Override
        public double x() {
            return this.entry.x();
        }

        @Override
        public double z() {
            return this.entry.z();
        }

        @Override
        public float rotation() {
            return this.entry.rotation();
        }
    }

    static final class BuilderImpl implements Builder {

        private final Map<String, net.minecraft.component.type.MapDecorationsComponent.Decoration> entries = new Object2ObjectOpenHashMap<>();

        @Override
        public MapDecorations.Builder put(final String id, final DecorationEntry entry) {
            this.entries.put(id, new net.minecraft.component.type.MapDecorationsComponent.Decoration(CraftMapCursor.CraftType.bukkitToMinecraftHolder(entry.type()), entry.x(), entry.z(), entry.rotation()));
            return this;
        }

        @Override
        public Builder putAll(final Map<String, DecorationEntry> entries) {
            entries.forEach(this::put);
            return this;
        }

        @Override
        public MapDecorations build() {
            if (this.entries.isEmpty()) {
                return new PaperMapDecorations(net.minecraft.component.type.MapDecorationsComponent.DEFAULT);
            }
            return new PaperMapDecorations(new net.minecraft.component.type.MapDecorationsComponent(new Object2ObjectOpenHashMap<>(this.entries)));
        }
    }
}

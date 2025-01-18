package io.papermc.paper.datacomponent.item;

import org.bukkit.Color;
import org.bukkit.craftbukkit.util.Handleable;

public record PaperMapItemColor(
    net.minecraft.component.type.MapColorComponent impl
) implements MapItemColor, Handleable<net.minecraft.component.type.MapColorComponent> {

    @Override
    public net.minecraft.component.type.MapColorComponent getHandle() {
        return this.impl;
    }

    @Override
    public Color color() {
        return Color.fromRGB(this.impl.rgb() & 0x00FFFFFF); // skip alpha channel
    }

    static final class BuilderImpl implements Builder {

        private Color color = Color.fromRGB(net.minecraft.component.type.MapColorComponent.DEFAULT.rgb());

        @Override
        public Builder color(final Color color) {
            this.color = color;
            return this;
        }

        @Override
        public MapItemColor build() {
            return new PaperMapItemColor(new net.minecraft.component.type.MapColorComponent(this.color.asRGB()));
        }
    }
}

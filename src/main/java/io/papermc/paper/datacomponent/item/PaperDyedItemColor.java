package io.papermc.paper.datacomponent.item;

import org.bukkit.Color;
import org.bukkit.craftbukkit.util.Handleable;

public record PaperDyedItemColor(
    net.minecraft.component.type.DyedColorComponent impl
) implements DyedItemColor, Handleable<net.minecraft.component.type.DyedColorComponent> {

    @Override
    public net.minecraft.component.type.DyedColorComponent getHandle() {
        return this.impl;
    }

    @Override
    public Color color() {
        return Color.fromRGB(this.impl.rgb() & 0x00FFFFFF); // skip alpha channel
    }

    @Override
    public boolean showInTooltip() {
        return false; // this.impl.showInTooltip();
    }

    @Override
    public DyedItemColor showInTooltip(final boolean showInTooltip) {
        // return new PaperDyedItemColor(this.impl.withShowInTooltip(showInTooltip));
    	return new PaperDyedItemColor(this.impl);
    }

    static final class BuilderImpl implements DyedItemColor.Builder {

        private Color color = Color.WHITE;
        private boolean showInToolTip = true;

        @Override
        public DyedItemColor.Builder color(final Color color) {
            this.color = color;
            return this;
        }

        @Override
        public DyedItemColor.Builder showInTooltip(final boolean showInTooltip) {
            this.showInToolTip = showInTooltip;
            return this;
        }

        @Override
        public DyedItemColor build() {
            return new PaperDyedItemColor(new net.minecraft.component.type.DyedColorComponent(this.color.asRGB()/*, this.showInToolTip*/));
        }
    }
}

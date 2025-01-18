package io.papermc.paper.datacomponent.item;

import com.google.common.base.Preconditions;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.util.MCUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.Unmodifiable;

public record PaperItemLore(
    net.minecraft.component.type.LoreComponent impl
) implements ItemLore, Handleable<net.minecraft.component.type.LoreComponent> {

    @Override
    public net.minecraft.component.type.LoreComponent getHandle() {
        return this.impl;
    }

    @Override
    public @Unmodifiable List<Component> lines() {
        return MCUtil.transformUnmodifiable(this.impl.lines(), PaperAdventure::asAdventure);
    }

    @Override
    public @Unmodifiable List<Component> styledLines() {
        return MCUtil.transformUnmodifiable(this.impl.styledLines(), PaperAdventure::asAdventure);
    }

    static final class BuilderImpl implements ItemLore.Builder {

        private List<Component> lines = new ObjectArrayList<>();

        private static void validateLineCount(final int current, final int add) {
            final int newSize = current + add;
            Preconditions.checkArgument(
                newSize <= net.minecraft.component.type.LoreComponent.MAX_LORES,
                "Cannot have more than %s lines, had %s",
                net.minecraft.component.type.LoreComponent.MAX_LORES,
                newSize
            );
        }

        @Override
        public ItemLore.Builder lines(final List<? extends ComponentLike> lines) {
            validateLineCount(0, lines.size());
            this.lines = new ArrayList<>(ComponentLike.asComponents(lines));
            return this;
        }

        @Override
        public ItemLore.Builder addLine(final ComponentLike line) {
            validateLineCount(this.lines.size(), 1);
            this.lines.add(line.asComponent());
            return this;
        }

        @Override
        public ItemLore.Builder addLines(final List<? extends ComponentLike> lines) {
            validateLineCount(this.lines.size(), lines.size());
            this.lines.addAll(ComponentLike.asComponents(lines));
            return this;
        }

        @Override
        public ItemLore build() {
            if (this.lines.isEmpty()) {
                return new PaperItemLore(net.minecraft.component.type.LoreComponent.DEFAULT);
            }

            return new PaperItemLore(new net.minecraft.component.type.LoreComponent(PaperAdventure.asVanilla(this.lines))); // asVanilla does a list clone
        }
    }
}

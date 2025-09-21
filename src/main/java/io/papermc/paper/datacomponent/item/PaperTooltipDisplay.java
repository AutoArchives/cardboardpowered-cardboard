package io.papermc.paper.datacomponent.item;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.PaperDataComponentType;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.component.type.TooltipDisplayComponent;
import org.bukkit.craftbukkit.util.Handleable;

public record PaperTooltipDisplay(TooltipDisplayComponent impl) implements TooltipDisplay, Handleable<TooltipDisplayComponent> {

    @Override
    public TooltipDisplayComponent getHandle() {
        return this.impl;
    }

    public boolean hideTooltip() {
        return this.impl.hideTooltip();
    }

    public Set<DataComponentType> hiddenComponents() {
        return this.impl.hiddenComponents().stream().map(PaperDataComponentType::minecraftToBukkit).collect(Collectors.toCollection(ReferenceLinkedOpenHashSet::new));
    }

    static final class BuilderImpl implements TooltipDisplay.Builder {
        private final Set<DataComponentType> hiddenComponents = new ReferenceLinkedOpenHashSet();
        private boolean hideTooltip;

        BuilderImpl() {
        }

        public TooltipDisplay.Builder hideTooltip(boolean hide) {
            this.hideTooltip = hide;
            return this;
        }

        public TooltipDisplay.Builder addHiddenComponents(DataComponentType ... components) {
            this.hiddenComponents.addAll(Arrays.asList(components));
            return this;
        }

        public TooltipDisplay.Builder hiddenComponents(Set<DataComponentType> components) {
            this.hiddenComponents.addAll(components);
            return this;
        }

        public TooltipDisplay build() {
            return new PaperTooltipDisplay(new TooltipDisplayComponent(this.hideTooltip, this.hiddenComponents.stream().map(PaperDataComponentType::bukkitToMinecraft).collect(Collectors.toCollection(ReferenceLinkedOpenHashSet::new))));
        }
    }

}
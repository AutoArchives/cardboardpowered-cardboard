package io.papermc.paper.datacomponent.item;

import io.papermc.paper.potion.SuspiciousEffectEntry;
import io.papermc.paper.util.MCUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.Unmodifiable;

import static io.papermc.paper.potion.SuspiciousEffectEntry.create;

public record PaperSuspiciousStewEffects(
    net.minecraft.component.type.SuspiciousStewEffectsComponent impl
) implements SuspiciousStewEffects, Handleable<net.minecraft.component.type.SuspiciousStewEffectsComponent> {

    @Override
    public net.minecraft.component.type.SuspiciousStewEffectsComponent getHandle() {
        return this.impl;
    }

    @Override
    public @Unmodifiable List<SuspiciousEffectEntry> effects() {
        return MCUtil.transformUnmodifiable(this.impl.effects(), entry -> create(CraftPotionEffectType.minecraftHolderToBukkit(entry.effect()), entry.duration()));
    }

    static final class BuilderImpl implements Builder {

        private final List<net.minecraft.component.type.SuspiciousStewEffectsComponent.StewEffect> effects = new ObjectArrayList<>();

        @Override
        public Builder add(final SuspiciousEffectEntry entry) {
            this.effects.add(new net.minecraft.component.type.SuspiciousStewEffectsComponent.StewEffect(
                org.bukkit.craftbukkit.potion.CraftPotionEffectType.bukkitToMinecraftHolder(entry.effect()),
                entry.duration()
            ));
            return this;
        }

        @Override
        public Builder addAll(final Collection<SuspiciousEffectEntry> entries) {
            entries.forEach(this::add);
            return this;
        }

        @Override
        public SuspiciousStewEffects build() {
            if (this.effects.isEmpty()) {
                return new PaperSuspiciousStewEffects(net.minecraft.component.type.SuspiciousStewEffectsComponent.DEFAULT);
            }

            return new PaperSuspiciousStewEffects(
                new net.minecraft.component.type.SuspiciousStewEffectsComponent(new ObjectArrayList<>(this.effects))
            );
        }
    }
}

package io.papermc.paper.datacomponent.item;

import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.PaperConsumableEffects;
import io.papermc.paper.util.MCUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.Unmodifiable;

public record PaperDeathProtection(
    net.minecraft.component.type.DeathProtectionComponent impl
) implements DeathProtection, Handleable<net.minecraft.component.type.DeathProtectionComponent> {

    @Override
    public net.minecraft.component.type.DeathProtectionComponent getHandle() {
        return this.impl;
    }

    @Override
    public @Unmodifiable List<ConsumeEffect> deathEffects() {
        return MCUtil.transformUnmodifiable(this.impl.deathEffects(), PaperConsumableEffects::fromNms);
    }

    static final class BuilderImpl implements Builder {

        private final List<net.minecraft.item.consume.ConsumeEffect> effects = new ArrayList<>();

        @Override
        public Builder addEffect(final ConsumeEffect effect) {
            this.effects.add(PaperConsumableEffects.toNms(effect));
            return this;
        }

        @Override
        public Builder addEffects(final List<ConsumeEffect> effects) {
            for (final ConsumeEffect effect : effects) {
                this.effects.add(PaperConsumableEffects.toNms(effect));
            }
            return this;
        }

        @Override
        public DeathProtection build() {
            return new PaperDeathProtection(
                new net.minecraft.component.type.DeathProtectionComponent(this.effects)
            );
        }
    }
}

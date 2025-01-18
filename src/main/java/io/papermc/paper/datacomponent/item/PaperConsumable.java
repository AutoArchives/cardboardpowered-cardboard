package io.papermc.paper.datacomponent.item;

import com.google.common.base.Preconditions;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.datacomponent.item.consumable.PaperConsumableEffects;
import io.papermc.paper.util.MCUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.bukkit.craftbukkit.util.Handleable;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.Unmodifiable;

public record PaperConsumable(
    net.minecraft.component.type.ConsumableComponent impl
) implements Consumable, Handleable<net.minecraft.component.type.ConsumableComponent> {

    private static final ItemUseAnimation[] VALUES = ItemUseAnimation.values();

    @Override
    public net.minecraft.component.type.ConsumableComponent getHandle() {
        return this.impl;
    }

    @Override
    public @NonNegative float consumeSeconds() {
        return this.impl.consumeSeconds();
    }

    @Override
    public ItemUseAnimation animation() {
        return VALUES[this.impl.useAction().ordinal()];
    }

    @Override
    public Key sound() {
        return PaperAdventure.asAdventure(this.impl.sound().value().id());
    }

    @Override
    public boolean hasConsumeParticles() {
        return this.impl.hasConsumeParticles();
    }

    @Override
    public @Unmodifiable List<ConsumeEffect> consumeEffects() {
        return MCUtil.transformUnmodifiable(this.impl.onConsumeEffects(), PaperConsumableEffects::fromNms);
    }

    @Override
    public Consumable.Builder toBuilder() {
        return new BuilderImpl()
            .consumeSeconds(this.consumeSeconds())
            .animation(this.animation())
            .sound(this.sound())
            .addEffects(this.consumeEffects());
    }

    static final class BuilderImpl implements Builder {

        private static final net.minecraft.item.consume.UseAction[] VALUES = net.minecraft.item.consume.UseAction.values();

        private float consumeSeconds = net.minecraft.component.type.ConsumableComponent.DEFAULT_CONSUME_SECONDS;
        private net.minecraft.item.consume.UseAction consumeAnimation = net.minecraft.item.consume.UseAction.EAT;
        private RegistryEntry<SoundEvent> eatSound = SoundEvents.ENTITY_GENERIC_EAT;
        private boolean hasConsumeParticles = true;
        private final List<net.minecraft.item.consume.ConsumeEffect> effects = new ObjectArrayList<>();

        @Override
        public Builder consumeSeconds(final @NonNegative float consumeSeconds) {
            Preconditions.checkArgument(consumeSeconds >= 0, "consumeSeconds must be non-negative, was %s", consumeSeconds);
            this.consumeSeconds = consumeSeconds;
            return this;
        }

        @Override
        public Builder animation(final ItemUseAnimation animation) {
            this.consumeAnimation = VALUES[animation.ordinal()];
            return this;
        }

        @Override
        public Builder sound(final Key sound) {
            this.eatSound = PaperAdventure.resolveSound(sound);
            return this;
        }

        @Override
        public Builder hasConsumeParticles(final boolean hasConsumeParticles) {
            this.hasConsumeParticles = hasConsumeParticles;
            return this;
        }

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
        public Consumable build() {
            return new PaperConsumable(
                new net.minecraft.component.type.ConsumableComponent(
                    this.consumeSeconds,
                    this.consumeAnimation,
                    this.eatSound,
                    this.hasConsumeParticles,
                    new ObjectArrayList<>(this.effects)
                )
            );
        }
    }
}

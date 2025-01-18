package io.papermc.paper.datacomponent.item;

import com.google.common.base.Preconditions;
import io.papermc.paper.util.MCUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.component.type.FireworkExplosionComponent;
import org.bukkit.FireworkEffect;
import org.bukkit.craftbukkit.inventory.CraftMetaFirework;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.Unmodifiable;

public record PaperFireworks(
    net.minecraft.component.type.FireworksComponent impl
) implements Fireworks, Handleable<net.minecraft.component.type.FireworksComponent> {

    @Override
    public net.minecraft.component.type.FireworksComponent getHandle() {
        return this.impl;
    }

    @Override
    public @Unmodifiable List<FireworkEffect> effects() {
        return MCUtil.transformUnmodifiable(this.impl.explosions(), CraftMetaFirework::getEffect);
    }

    @Override
    public int flightDuration() {
        return this.impl.flightDuration();
    }

    static final class BuilderImpl implements Fireworks.Builder {

        private final List<FireworkExplosionComponent> effects = new ObjectArrayList<>();
        private int duration = 0; // default set from nms Fireworks component

        @Override
        public Fireworks.Builder flightDuration(final int duration) {
            Preconditions.checkArgument(duration >= 0 && duration <= 0xFF, "duration must be an unsigned byte ([%s, %s]), was %s", 0, 0xFF, duration);
            this.duration = duration;
            return this;
        }

        @Override
        public Fireworks.Builder addEffect(final FireworkEffect effect) {
            Preconditions.checkArgument(
                this.effects.size() + 1 <= net.minecraft.component.type.FireworksComponent.MAX_EXPLOSIONS,
                "Cannot have more than %s effects, had %s",
                net.minecraft.component.type.FireworksComponent.MAX_EXPLOSIONS,
                this.effects.size() + 1
            );
            this.effects.add(CraftMetaFirework.getExplosion(effect));
            return this;
        }

        @Override
        public Fireworks.Builder addEffects(final List<FireworkEffect> effects) {
            Preconditions.checkArgument(
                this.effects.size() + effects.size() <= net.minecraft.component.type.FireworksComponent.MAX_EXPLOSIONS,
                "Cannot have more than %s effects, had %s",
                net.minecraft.component.type.FireworksComponent.MAX_EXPLOSIONS,
                this.effects.size() + effects.size()
            );
            MCUtil.addAndConvert(this.effects, effects, CraftMetaFirework::getExplosion);
            return this;
        }

        @Override
        public Fireworks build() {
            return new PaperFireworks(new net.minecraft.component.type.FireworksComponent(this.duration, new ObjectArrayList<>(this.effects)));
        }
    }
}

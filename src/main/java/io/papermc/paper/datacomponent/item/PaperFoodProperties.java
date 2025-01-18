package io.papermc.paper.datacomponent.item;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.util.Handleable;

public record PaperFoodProperties(
    net.minecraft.component.type.FoodComponent impl
) implements FoodProperties, Handleable<net.minecraft.component.type.FoodComponent> {

    @Override
    public int nutrition() {
        return this.impl.nutrition();
    }

    @Override
    public float saturation() {
        return this.impl.saturation();
    }

    @Override
    public boolean canAlwaysEat() {
        return this.impl.canAlwaysEat();
    }

    @Override
    public FoodProperties.Builder toBuilder() {
        return new BuilderImpl()
            .nutrition(this.nutrition())
            .saturation(this.saturation())
            .canAlwaysEat(this.canAlwaysEat());
    }

    @Override
    public net.minecraft.component.type.FoodComponent getHandle() {
        return this.impl;
    }

    static final class BuilderImpl implements FoodProperties.Builder {

        private boolean canAlwaysEat = false;
        private float saturation = 0;
        private int nutrition = 0;

        @Override
        public FoodProperties.Builder canAlwaysEat(final boolean canAlwaysEat) {
            this.canAlwaysEat = canAlwaysEat;
            return this;
        }

        @Override
        public FoodProperties.Builder saturation(final float saturation) {
            this.saturation = saturation;
            return this;
        }

        @Override
        public FoodProperties.Builder nutrition(final int nutrition) {
            Preconditions.checkArgument(nutrition >= 0, "nutrition must be non-negative, was %s", nutrition);
            this.nutrition = nutrition;
            return this;
        }

        @Override
        public FoodProperties build() {
            return new PaperFoodProperties(new net.minecraft.component.type.FoodComponent(
                this.nutrition,
                this.saturation,
                this.canAlwaysEat
            ));
        }
    }
}

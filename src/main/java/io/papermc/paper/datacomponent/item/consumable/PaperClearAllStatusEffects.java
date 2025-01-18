package io.papermc.paper.datacomponent.item.consumable;

public record PaperClearAllStatusEffects(
    net.minecraft.item.consume.ClearAllEffectsConsumeEffect impl
) implements ConsumeEffect.ClearAllStatusEffects, PaperConsumableEffectImpl<net.minecraft.item.consume.ClearAllEffectsConsumeEffect> {

    @Override
    public net.minecraft.item.consume.ClearAllEffectsConsumeEffect getHandle() {
        return this.impl;
    }
}

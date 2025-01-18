package io.papermc.paper.datacomponent.item.consumable;

public record PaperTeleportRandomly(
    net.minecraft.item.consume.TeleportRandomlyConsumeEffect impl
) implements ConsumeEffect.TeleportRandomly, PaperConsumableEffectImpl<net.minecraft.item.consume.TeleportRandomlyConsumeEffect> {
    @Override
    public float diameter() {
        return this.impl.diameter();
    }

    @Override
    public net.minecraft.item.consume.TeleportRandomlyConsumeEffect getHandle() {
        return this.impl;
    }
}

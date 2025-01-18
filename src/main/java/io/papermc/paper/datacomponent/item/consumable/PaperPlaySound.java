package io.papermc.paper.datacomponent.item.consumable;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.key.Key;
import net.minecraft.item.consume.PlaySoundConsumeEffect;

public record PaperPlaySound(
    PlaySoundConsumeEffect impl
) implements ConsumeEffect.PlaySound, PaperConsumableEffectImpl<PlaySoundConsumeEffect> {

    @Override
    public Key sound() {
        return PaperAdventure.asAdventure(this.impl.sound().value().id());
    }

    @Override
    public PlaySoundConsumeEffect getHandle() {
        return this.impl;
    }
}

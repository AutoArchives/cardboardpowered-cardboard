package io.papermc.paper.datacomponent.item.consumable;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.PaperRegistrySets;
import io.papermc.paper.registry.set.RegistryKeySet;
import org.bukkit.potion.PotionEffectType;

public record PaperRemoveStatusEffects(
    net.minecraft.item.consume.RemoveEffectsConsumeEffect impl
) implements ConsumeEffect.RemoveStatusEffects, PaperConsumableEffectImpl<net.minecraft.item.consume.RemoveEffectsConsumeEffect> {

    @Override
    public RegistryKeySet<PotionEffectType> removeEffects() {
        return PaperRegistrySets.convertToApi(RegistryKey.MOB_EFFECT, this.impl.effects());
    }

    @Override
    public net.minecraft.item.consume.RemoveEffectsConsumeEffect getHandle() {
        return this.impl;
    }
}

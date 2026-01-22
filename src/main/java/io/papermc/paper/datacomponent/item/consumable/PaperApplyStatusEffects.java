package io.papermc.paper.datacomponent.item.consumable;

import java.util.List;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import org.bukkit.potion.PotionEffect;
import org.cardboardpowered.impl.CardboardPotionUtil;

import static io.papermc.paper.util.MCUtil.transformUnmodifiable;

public record PaperApplyStatusEffects(
    ApplyStatusEffectsConsumeEffect impl
) implements ConsumeEffect.ApplyStatusEffects, PaperConsumableEffectImpl<ApplyStatusEffectsConsumeEffect> {

    @Override
    public List<PotionEffect> effects() {
        return transformUnmodifiable(this.impl().effects(), CardboardPotionUtil::toBukkit);
    }

    @Override
    public float probability() {
        return this.impl.probability();
    }

    @Override
    public ApplyStatusEffectsConsumeEffect getHandle() {
        return this.impl;
    }
}

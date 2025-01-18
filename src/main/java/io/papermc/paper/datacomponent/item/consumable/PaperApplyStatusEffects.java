package io.papermc.paper.datacomponent.item.consumable;

import java.util.List;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import org.bukkit.potion.PotionEffect;
import org.cardboardpowered.impl.CardboardPotionUtil;

import static io.papermc.paper.util.MCUtil.transformUnmodifiable;

public record PaperApplyStatusEffects(
    ApplyEffectsConsumeEffect impl
) implements ConsumeEffect.ApplyStatusEffects, PaperConsumableEffectImpl<ApplyEffectsConsumeEffect> {

    @Override
    public List<PotionEffect> effects() {
        return transformUnmodifiable(this.impl().effects(), CardboardPotionUtil::toBukkit);
    }

    @Override
    public float probability() {
        return this.impl.probability();
    }

    @Override
    public ApplyEffectsConsumeEffect getHandle() {
        return this.impl;
    }
}

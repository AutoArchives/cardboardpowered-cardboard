package io.papermc.paper.datacomponent.item.consumable;

import net.minecraft.item.consume.*;

public final class PaperConsumableEffects {

    private PaperConsumableEffects() {
    }

    public static ConsumeEffect fromNms(net.minecraft.item.consume.ConsumeEffect consumable) {
        return switch (consumable) {
            case ApplyEffectsConsumeEffect effect -> new PaperApplyStatusEffects(effect);
            case ClearAllEffectsConsumeEffect effect -> new PaperClearAllStatusEffects(effect);
            case PlaySoundConsumeEffect effect -> new PaperPlaySound(effect);
            case RemoveEffectsConsumeEffect effect -> new PaperRemoveStatusEffects(effect);
            case TeleportRandomlyConsumeEffect effect -> new PaperTeleportRandomly(effect);
            default -> throw new UnsupportedOperationException("Don't know how to convert " + consumable.getClass());
        };
    }

    public static net.minecraft.item.consume.ConsumeEffect toNms(ConsumeEffect effect) {
        if (effect instanceof PaperConsumableEffectImpl<?> consumableEffect) {
            return consumableEffect.getHandle();
        } else {
            throw new UnsupportedOperationException("Must implement handleable!");
        }
    }
}

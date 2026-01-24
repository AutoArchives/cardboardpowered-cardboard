package org.cardboardpowered.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.effect.MobEffects;
import org.cardboardpowered.impl.CardboardPotionEffectType;

@Mixin(MobEffects.class)
public class MixinStatusEffects {

    static {
        //for (Object effect : Registry.STATUS_EFFECT) {
       //     org.bukkit.potion.PotionEffectType.registerPotionEffectType(new CardboardPotionEffectType((StatusEffect) effect));
       // }
    }

}
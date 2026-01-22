package org.cardboardpowered.mixin.entity;

import java.util.Collection;
import java.util.Set;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import org.cardboardpowered.interfaces.IMixinArrowEntity;

@Mixin(Arrow.class)
public class MixinArrowEntity implements IMixinArrowEntity {

    //@Shadow
    //public Potion potion;

    //@Shadow
    //public Set<StatusEffectInstance> effects;

    //@Shadow
    //private static TrackedData<Integer> COLOR;

    @Override
    public void setType(String string) {
        // TODO: 1.20.5
    	// this.potion = Registries.POTION.get(new Identifier(string));
        // (((Entity)(Object)this).getDataTracker()).set(COLOR, PotionUtil.getColor((Collection<StatusEffectInstance>) PotionUtil.getPotionEffects(this.potion, (Collection<StatusEffectInstance>) this.effects)));
    }

}

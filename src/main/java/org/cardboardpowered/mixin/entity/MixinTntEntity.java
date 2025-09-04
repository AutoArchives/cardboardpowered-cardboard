package org.cardboardpowered.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.cardboardpowered.interfaces.ITnt;

import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;

@Mixin(TntEntity.class)
public class MixinTntEntity implements ITnt {

	/**
	 * @implNote LivingEntity (1.21.4) -> LazyEntityReference<LivingEntity> (1.21.8)
	 */
	@Shadow
	public LazyEntityReference<LivingEntity> causingEntity;

    @Override
    public void cardboard$setSource(LivingEntity entity) {
        this.causingEntity = entity != null ? new LazyEntityReference<LivingEntity>(entity) : null;;
    }

}

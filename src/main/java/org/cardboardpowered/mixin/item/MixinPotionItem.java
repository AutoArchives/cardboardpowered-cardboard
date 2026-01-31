package org.cardboardpowered.mixin.item;

import net.minecraft.world.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = PotionItem.class, priority = 900)
public class MixinPotionItem {

	/*
    @Inject(
    		method = "finishUsing",
    		at = @At(
    				value = "INVOKE",
    				target = "Lnet/minecraft/component/type/PotionContentsComponent;forEachEffect(Ljava/util/function/Consumer;)V"
    			)
    	)
    public void cardboard$potionitem_set_effect_event_cause(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        ((IMixinLivingEntity) user).pushEffectCause(EntityPotionEffectEvent.Cause.POTION_DRINK);
    }
    */

}
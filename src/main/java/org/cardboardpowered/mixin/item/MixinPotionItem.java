package org.cardboardpowered.mixin.item;

import com.javazilla.bukkitfabric.interfaces.IMixinLivingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.world.World;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PotionItem.class, priority = 900)
public class MixinPotionItem {

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
}

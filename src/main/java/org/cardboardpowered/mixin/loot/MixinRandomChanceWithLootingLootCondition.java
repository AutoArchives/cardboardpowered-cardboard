package org.cardboardpowered.mixin.loot;

import com.javazilla.bukkitfabric.interfaces.IMixinLootContextParameters;
import net.minecraft.loot.condition.RandomChanceWithEnchantedBonusLootCondition;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RandomChanceWithEnchantedBonusLootCondition.class)
public class MixinRandomChanceWithLootingLootCondition {
    //@Final @Shadow private float chance;
    //@Final @Shadow private float lootingMultiplier;

	// Lnet/minecraft/loot/condition/RandomChanceWithEnchantedBonusLootCondition;enchantedChance:Lnet/minecraft/enchantment/EnchantmentLevelBasedValue;
	
    @Inject(at = @At("RETURN"), method = "test(Lnet/minecraft/loot/context/LootContext;)Z", cancellable = true)
    public void cardboard_test(LootContext loottableinfo, CallbackInfoReturnable<Boolean> ci) {
        if (loottableinfo.hasParameter(IMixinLootContextParameters.LOOTING_MOD)) {
            int i = loottableinfo.get(IMixinLootContextParameters.LOOTING_MOD);
            
            RandomChanceWithEnchantedBonusLootCondition thiz = (RandomChanceWithEnchantedBonusLootCondition) (Object) this;
            
            float f2 = i > 0 ? thiz.enchantedChance().getValue(i) : thiz.unenchantedChance();
            
            ci.setReturnValue(loottableinfo.getRandom().nextFloat() < f2);
            return;
        }
    }

}

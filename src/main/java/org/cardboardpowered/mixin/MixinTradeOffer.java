package org.cardboardpowered.mixin;

import net.minecraft.world.item.trading.MerchantOffer;
import org.bukkit.craftbukkit.inventory.CraftMerchantRecipe;
import org.spongepowered.asm.mixin.Mixin;

import org.cardboardpowered.interfaces.IMixinTradeOffer;

@Mixin(MerchantOffer.class)
public class MixinTradeOffer implements IMixinTradeOffer {

    private CraftMerchantRecipe bukkitHandle;

    @Override
    public CraftMerchantRecipe asBukkit() {
        return (bukkitHandle == null) ? bukkitHandle = new CraftMerchantRecipe((MerchantOffer)(Object)this) : bukkitHandle;
    }

}
package org.cardboardpowered.mixin.entity;

import org.bukkit.craftbukkit.inventory.CraftMerchant;
import org.spongepowered.asm.mixin.Mixin;
import org.cardboardpowered.impl.entity.CraftAbstractVillager;
import org.cardboardpowered.interfaces.IMixinTrader;

import net.minecraft.entity.passive.MerchantEntity;

@Mixin(MerchantEntity.class)
public class MixinAbstractTraderEntity implements IMixinTrader {

    // private CraftMerchant craftMerchant;

    @Override
    public CraftMerchant getCraftMerchant() {
    	
    	return (CraftAbstractVillager) ((MerchantEntity)(Object)this).getBukkitEntity();
    	
        // return (craftMerchant == null) ? craftMerchant = new CraftMerchant((MerchantEntity)(Object) this) : craftMerchant;
    }

}

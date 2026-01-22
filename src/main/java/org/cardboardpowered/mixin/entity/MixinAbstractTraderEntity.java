package org.cardboardpowered.mixin.entity;

import net.minecraft.world.entity.npc.villager.AbstractVillager;
import org.bukkit.craftbukkit.inventory.CraftMerchant;
import org.spongepowered.asm.mixin.Mixin;
import org.cardboardpowered.impl.entity.CraftAbstractVillager;
import org.cardboardpowered.interfaces.IMixinTrader;

@Mixin(AbstractVillager.class)
public class MixinAbstractTraderEntity implements IMixinTrader {

    // private CraftMerchant craftMerchant;

    @Override
    public CraftMerchant getCraftMerchant() {
    	
    	return (CraftAbstractVillager) ((AbstractVillager)(Object)this).getBukkitEntity();
    	
        // return (craftMerchant == null) ? craftMerchant = new CraftMerchant((MerchantEntity)(Object) this) : craftMerchant;
    }

}

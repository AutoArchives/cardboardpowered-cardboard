package org.bukkit.craftbukkit.inventory.view;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.screen.MerchantScreenHandler;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.view.MerchantView;
import org.cardboardpowered.impl.entity.AbstractVillagerImpl;
import org.jetbrains.annotations.NotNull;

import com.javazilla.bukkitfabric.interfaces.IMixinEntity;

public class CraftMerchantView extends CraftInventoryView<MerchantScreenHandler, MerchantInventory> implements MerchantView {

    private final net.minecraft.village.Merchant trader;

    public CraftMerchantView(final HumanEntity player, final MerchantInventory viewing, final MerchantScreenHandler container, final net.minecraft.village.Merchant trader) {
        super(player, viewing, container);
        this.trader = trader;
    }

    @NotNull
    @Override
    public Merchant getMerchant() {
    	
    	if (this.trader instanceof MerchantEntity) {
    		return (AbstractVillagerImpl) (((IMixinEntity)this.trader).getBukkitEntity());
    	}
    	
    	return null; // TODO
        // return this.trader.getCraftMerchant();
    }
}

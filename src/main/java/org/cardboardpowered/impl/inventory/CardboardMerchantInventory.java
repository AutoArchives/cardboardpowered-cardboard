package org.cardboardpowered.impl.inventory;

import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.trading.Merchant;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.inventory.MerchantRecipe;

import org.cardboardpowered.bridge.world.item.trading.MerchantOfferBridge;
import org.cardboardpowered.bridge.world.entity.npc.villager.AbstractVillagerBridge;

public class CardboardMerchantInventory extends CraftInventory implements org.bukkit.inventory.MerchantInventory {

    private final Merchant merchant;

    public CardboardMerchantInventory(Merchant merchant, MerchantContainer inventory) {
        super(inventory);
        this.merchant = merchant;
    }

    @Override
    public int getSelectedRecipeIndex() {
        return 0; // TODO 1.17ify getInventory().offerIndex;
    }

    @Override
    public MerchantRecipe getSelectedRecipe() {
        net.minecraft.world.item.trading.MerchantOffer nmsRecipe = getInventory().getActiveOffer();
        return (nmsRecipe == null) ? null : ((MerchantOfferBridge)nmsRecipe).asBukkit();
    }

    @Override
    public MerchantContainer getInventory() {
        return (MerchantContainer) inventory;
    }

    @Override
    public org.bukkit.inventory.Merchant getMerchant() {
        return ((AbstractVillagerBridge)merchant).getCraftMerchant();
    }

}
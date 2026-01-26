package org.cardboardpowered.mixin.screen;

import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.cardboardpowered.impl.inventory.CardboardMerchantInventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import org.bukkit.entity.Player;
import org.cardboardpowered.mixin.world.inventory.AbstractContainerMenuMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cardboardpowered.interfaces.IMixinEntity;

@Mixin(MerchantMenu.class)
public class MixinMerchantScreenHandler extends AbstractContainerMenuMixin {

    @Shadow public Merchant trader;
    @Shadow public MerchantContainer tradeContainer;

    private CardboardInventoryView bukkitEntity = null;
    private Inventory player;

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/trading/Merchant;)V", at = @At("TAIL"))
    public void setPlayerInv(int i, Inventory playerinventory, Merchant imerchant, CallbackInfo ci) {
        this.player = playerinventory;
    }

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity == null)
            bukkitEntity = new CardboardInventoryView((Player)((IMixinEntity)this.player.player).getBukkitEntity(), new CardboardMerchantInventory(trader, tradeContainer), (MerchantMenu)(Object)this);
        return bukkitEntity;
    }

}

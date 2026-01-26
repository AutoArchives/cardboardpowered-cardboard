package org.cardboardpowered.mixin.screen;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.bukkit.entity.Player;
import org.cardboardpowered.mixin.world.inventory.AbstractContainerMenuMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cardboardpowered.bridge.server.level.ServerPlayerBridge;

@Mixin(ShulkerBoxMenu.class)
public class MixinShulkerBoxScreenHandler extends AbstractContainerMenuMixin {

    @Shadow
    public Container container;

    private CardboardInventoryView bukkitEntity;
    private Inventory inventory;

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;)V", at = @At("TAIL"))
    public void setPlayerInv(int i, Inventory playerinventory, Container iinventory, CallbackInfo ci) {
        this.inventory = (Inventory) playerinventory;
    }

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity != null)
            return bukkitEntity;

        bukkitEntity = new CardboardInventoryView((Player)((ServerPlayerBridge)this.inventory.player).getBukkitEntity(), new CraftInventory(this.container), (ShulkerBoxMenu)(Object)this);
        return bukkitEntity;
    }

    @Override
    public void cardboard$startOpen() {
        this.container.startOpen(this.inventory.player);
    }
}

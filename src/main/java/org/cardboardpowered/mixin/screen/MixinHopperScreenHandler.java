package org.cardboardpowered.mixin.screen;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HopperMenu;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cardboardpowered.interfaces.IMixinEntity;

@Mixin(HopperMenu.class)
public class MixinHopperScreenHandler extends MixinScreenHandler {

    @Shadow
    public Container hopper;

    private CardboardInventoryView bukkitEntity = null;
    private Inventory playerInv;

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;)V", at = @At("TAIL"))
    public void setPlayerInv(int i, Inventory playerinventory, Container iinventory, CallbackInfo ci) {
        this.playerInv = playerinventory;
    }

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity != null)
            return bukkitEntity;

        CraftInventory inventory = new CraftInventory(this.hopper);
        bukkitEntity = new CardboardInventoryView((Player)((IMixinEntity)this.playerInv.player).getBukkitEntity(), inventory, (HopperMenu)(Object)this);
        return bukkitEntity;
    }

}
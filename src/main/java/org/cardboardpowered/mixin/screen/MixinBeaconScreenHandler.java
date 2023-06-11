package org.cardboardpowered.mixin.screen;

import org.cardboardpowered.impl.inventory.CardboardBeaconInventory;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.bukkit.entity.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.javazilla.bukkitfabric.interfaces.IMixinServerEntityPlayer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;

@Mixin(BeaconScreenHandler.class)
public class MixinBeaconScreenHandler extends MixinScreenHandler {

    @Shadow
    public Inventory payment;

    private CardboardInventoryView bukkitEntity = null;
    private PlayerInventory player;

    @Inject(method = "<init>(ILnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At("TAIL"))
    public void setPlayerInv(int i, Inventory inventory, PropertyDelegate icontainerproperties, ScreenHandlerContext containeraccess, CallbackInfo ci) {
        this.player = (PlayerInventory) inventory;
    }

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity != null)
            return bukkitEntity;

        CardboardBeaconInventory inventory = new CardboardBeaconInventory(this.payment);
        bukkitEntity = new CardboardInventoryView((Player)((IMixinServerEntityPlayer)this.player.player).getBukkitEntity(), inventory, (BeaconScreenHandler)(Object)this);
        return bukkitEntity;
    }

}
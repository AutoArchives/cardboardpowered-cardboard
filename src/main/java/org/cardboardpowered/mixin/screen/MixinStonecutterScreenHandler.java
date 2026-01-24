package org.cardboardpowered.mixin.screen;

import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.cardboardpowered.impl.inventory.CardboardStonecutterInventory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.StonecutterMenu;
import org.bukkit.entity.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;

@Mixin(StonecutterMenu.class)
public class MixinStonecutterScreenHandler extends MixinScreenHandler {

    private CardboardInventoryView bukkitEntity = null;
    private Player player;

    @Shadow public Container container;
    @Shadow public ResultContainer resultContainer;

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
    public void setPlayerInv(int i, Inventory playerinventory, final ContainerLevelAccess containeraccess, CallbackInfo ci) {
        this.player = (Player)((IMixinServerEntityPlayer)playerinventory.player).getBukkitEntity();
    }

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity != null)
            return bukkitEntity;

        CardboardStonecutterInventory inventory = new CardboardStonecutterInventory(this.container, this.resultContainer);
        bukkitEntity = new CardboardInventoryView(this.player, inventory, (StonecutterMenu)(Object)this);
        return bukkitEntity;
    }

}

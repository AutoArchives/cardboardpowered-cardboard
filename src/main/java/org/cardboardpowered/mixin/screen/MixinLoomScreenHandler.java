package org.cardboardpowered.mixin.screen;

import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.cardboardpowered.impl.inventory.CardboardLoomInventory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.LoomMenu;
import org.bukkit.entity.Player;
import org.cardboardpowered.mixin.world.inventory.AbstractContainerMenuMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cardboardpowered.interfaces.IMixinEntity;

@Mixin(LoomMenu.class)
public class MixinLoomScreenHandler extends AbstractContainerMenuMixin {

    @Shadow public Container inputContainer;
    @Shadow public Container outputContainer;

    private CardboardInventoryView bukkitEntity = null;
    private Player player;

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
    public void setPlayerInv(int i, Inventory playerinventory, ContainerLevelAccess containeraccesss, CallbackInfo ci) {
        this.player = (Player)((IMixinEntity)playerinventory.player).getBukkitEntity();
    }

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity != null) return bukkitEntity;

        CardboardLoomInventory inventory = new CardboardLoomInventory(this.inputContainer, this.outputContainer);
        bukkitEntity = new CardboardInventoryView(this.player, inventory, (LoomMenu)(Object)this);
        return bukkitEntity;
    }

}
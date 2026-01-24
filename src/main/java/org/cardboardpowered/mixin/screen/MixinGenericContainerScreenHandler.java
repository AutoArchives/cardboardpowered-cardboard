package org.cardboardpowered.mixin.screen;

import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.cardboardpowered.impl.inventory.CardboardDoubleChestInventory;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.bukkit.entity.Player;
import org.cardboardpowered.impl.inventory.CardboardPlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;

@Mixin(ChestMenu.class)
public class MixinGenericContainerScreenHandler extends MixinScreenHandler {

    @Shadow
    public Container container;

    private CardboardInventoryView bukkitEntity = null;
    private Inventory player;

    @Inject(method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;I)V", at = @At("TAIL"))
    public void setPlayerInv(MenuType<?> containers, int i, Inventory playerinventory, Container inventory, int j, CallbackInfo ci) {
        this.player = (Inventory) playerinventory;
    }

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity != null)
            return bukkitEntity;

        CraftInventory inventory;
        if (this.container instanceof Inventory) {
            inventory = new CardboardPlayerInventory((Inventory) this.container);
        } else if (this.container instanceof CompoundContainer) {
            inventory = new CardboardDoubleChestInventory((CompoundContainer) this.container);
        } else inventory = new CraftInventory(this.container);

        bukkitEntity = new CardboardInventoryView((Player)((IMixinServerEntityPlayer)this.player.player).getBukkitEntity(), inventory, (ChestMenu)(Object)this);
        return bukkitEntity;
    }

}
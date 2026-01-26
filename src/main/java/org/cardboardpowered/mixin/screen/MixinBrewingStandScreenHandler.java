package org.cardboardpowered.mixin.screen;

import org.cardboardpowered.impl.inventory.CardboardBrewerInventory;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerData;
import org.bukkit.entity.Player;
import org.cardboardpowered.mixin.world.inventory.AbstractContainerMenuMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cardboardpowered.bridge.server.level.ServerPlayerBridge;

@Mixin(BrewingStandMenu.class)
public class MixinBrewingStandScreenHandler extends AbstractContainerMenuMixin {

    @Shadow
    public Container brewingStand;

    private CardboardInventoryView bukkitEntity = null;
    private Inventory player;

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V", at = @At("TAIL"))
    public void setPlayerInv(int i, Inventory inventory, Container iinventory, ContainerData icontainerproperties, CallbackInfo ci) {
        this.player = (Inventory) inventory;
    }

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity != null) return bukkitEntity;

        CardboardBrewerInventory inventory = new CardboardBrewerInventory(this.brewingStand);
        bukkitEntity = new CardboardInventoryView((Player)((ServerPlayerBridge)this.player.player).getBukkitEntity(), inventory, (BrewingStandMenu)(Object)this);
        return bukkitEntity;
    }

}
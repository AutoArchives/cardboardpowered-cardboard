package org.cardboardpowered.mixin.screen;

import org.cardboardpowered.impl.inventory.CardboardGrindstoneInventory;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import org.bukkit.entity.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;

@Mixin(GrindstoneMenu.class)
public class MixinGrindstoneScreenHandler extends MixinScreenHandler {

    private CardboardInventoryView bukkitEntity = null;
    private Player player;

    @Shadow private Container resultSlots;
    @Shadow private Container repairSlots;

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
    public void setPlayerInv(int i, Inventory playerinventory, final ContainerLevelAccess containeraccess, CallbackInfo ci) {
        this.player = (Player)((IMixinServerEntityPlayer)playerinventory.player).getBukkitEntity();
    }

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity != null)
            return bukkitEntity;

        CardboardGrindstoneInventory inventory = new CardboardGrindstoneInventory(this.repairSlots, this.resultSlots);
        bukkitEntity = new CardboardInventoryView(this.player, inventory, (GrindstoneMenu)(Object)this);
        return bukkitEntity;
    }

}

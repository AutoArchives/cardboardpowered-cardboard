package org.cardboardpowered.mixin.screen;

import org.cardboardpowered.impl.inventory.CardboardBeaconInventory;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.bukkit.entity.Player;
import org.cardboardpowered.mixin.world.inventory.AbstractContainerMenuMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cardboardpowered.bridge.server.level.ServerPlayerBridge;

@Mixin(BeaconMenu.class)
public class MixinBeaconScreenHandler extends AbstractContainerMenuMixin {

    @Shadow
    public Container beacon;

    private CardboardInventoryView bukkitEntity = null;
    private Inventory player;

    @Inject(method = "<init>(ILnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
    public void setPlayerInv(int i, Container inventory, ContainerData icontainerproperties, ContainerLevelAccess containeraccess, CallbackInfo ci) {
        this.player = (Inventory) inventory;
    }

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity != null)
            return bukkitEntity;

        CardboardBeaconInventory inventory = new CardboardBeaconInventory(this.beacon);
        bukkitEntity = new CardboardInventoryView((Player)((ServerPlayerBridge)this.player.player).getBukkitEntity(), inventory, (BeaconMenu)(Object)this);
        return bukkitEntity;
    }

}
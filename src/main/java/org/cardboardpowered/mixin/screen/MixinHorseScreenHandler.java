package org.cardboardpowered.mixin.screen;

import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;
import org.bukkit.entity.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cardboardpowered.interfaces.IMixinEntity;
import org.cardboardpowered.interfaces.IMixinInventory;

@Mixin(HorseInventoryMenu.class)
public class MixinHorseScreenHandler extends MixinScreenHandler {

    // @Shadow
    // public Inventory inventory;

    private CardboardInventoryView bukkitEntity = null;
    private Inventory playerInv;

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/animal/equine/AbstractHorse;I)V", at = @At("TAIL"))
    public void setPlayerInv(int i, Inventory playerinventory, Container iinventory, final AbstractHorse  entityhorseabstract, int slot_col_count, CallbackInfo ci) {
        this.playerInv = playerinventory;
    }

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity != null)
            return bukkitEntity;
        return bukkitEntity = new CardboardInventoryView((Player)((IMixinEntity)this.playerInv.player).getBukkitEntity(), ((IMixinInventory)playerInv).getOwner().getInventory(), (HorseInventoryMenu)(Object)this);
    }


}
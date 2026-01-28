package org.cardboardpowered.mixin.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import org.cardboardpowered.bridge.world.entity.EntityBridge;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;

@Mixin(value = ServerPlayer.class, priority = 900)
public class MixinPlayerEntity {
    
    // private ItemEntity cardboard_stored_entity;

	/*
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;setPickupDelay(I)V"),
            method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;")
    public void store_item_entity(ItemEntity ie, int i, net.minecraft.item.ItemStack stack, boolean z, boolean z2) {
        ie.setPickupDelay(i);
        cardboard_stored_entity = ie;
    }
    */

    @SuppressWarnings("deprecation")
    @Inject(at = @At("RETURN"),
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void cardboard_doPlayerDropItemEvent(
    		net.minecraft.world.item.ItemStack stack,
    		boolean throwRandomly,
    		boolean retainOwnership,
    		CallbackInfoReturnable<ItemEntity> ci,
    		@Local ItemEntity itemEntity
    ) {
        if (stack.isEmpty()) {
            return;
        }
        Player player = (Player)(((EntityBridge)this).getBukkitEntity());
        Item drop = (Item) ((EntityBridge)itemEntity).getBukkitEntity();
        PlayerDropItemEvent event = new PlayerDropItemEvent(player, drop);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ItemStack cur = player.getInventory().getItemInHand();
            if (retainOwnership && (cur == null || cur.getAmount() == 0)) {
                player.getInventory().setItemInHand(drop.getItemStack());
            } else if (retainOwnership && cur.isSimilar(drop.getItemStack()) && cur.getAmount() < cur.getMaxStackSize() && drop.getItemStack().getAmount() == 1) {
                cur.setAmount(cur.getAmount() + 1);
                player.getInventory().setItemInHand(cur);
            } else player.getInventory().addItem(drop.getItemStack());

            itemEntity = null;
            ci.setReturnValue(null);
        }
        // cardboard_stored_entity = null;
    }

}

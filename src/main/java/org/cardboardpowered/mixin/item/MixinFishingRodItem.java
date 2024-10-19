package org.cardboardpowered.mixin.item;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerFishEvent;
import org.cardboardpowered.util.MixinInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.javazilla.bukkitfabric.interfaces.IMixinEntity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

@MixinInfo(events = {"PlayerFishEvent"})
@Mixin(FishingRodItem.class)
public class MixinFishingRodItem {

    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    public void bukkitize(World world, PlayerEntity entityhuman, Hand enumhand, CallbackInfoReturnable<TypedActionResult<ItemStack>> ci) {
        if (null == entityhuman.fishHook) {
            ItemStack itemstack = entityhuman.getStackInHand(enumhand);

            int i = (int)(EnchantmentHelper.getFishingTimeReduction((ServerWorld) world, itemstack, entityhuman) * 20.0f);
            int j = EnchantmentHelper.getFishingLuckBonus((ServerWorld) world, itemstack, entityhuman);
            
            FishingBobberEntity entityfishinghook = new FishingBobberEntity(entityhuman, world, j, i);
            PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player) ((IMixinEntity)entityhuman).getBukkitEntity(), null, (org.bukkit.entity.FishHook) ((IMixinEntity)entityfishinghook).getBukkitEntity(), PlayerFishEvent.State.FISHING);
            Bukkit.getPluginManager().callEvent(playerFishEvent);
    
            if (playerFishEvent.isCancelled()) {
                entityhuman.fishHook = null;
                ci.setReturnValue( new TypedActionResult<ItemStack>(ActionResult.PASS, itemstack) );
                return;
            }
            world.spawnEntity(entityfishinghook); 
            ci.setReturnValue(TypedActionResult.success(itemstack, false));
            return;
        }
    }

}
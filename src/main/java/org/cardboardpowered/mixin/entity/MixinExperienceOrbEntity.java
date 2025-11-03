package org.cardboardpowered.mixin.entity;

import java.util.Optional;

import org.bukkit.event.player.PlayerExpCooldownChangeEvent.ChangeReason;
import org.cardboardpowered.interfaces.IMixinEntity;
import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;

import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(value = ExperienceOrbEntity.class, priority = 900)
public class MixinExperienceOrbEntity extends MixinEntity {

	private static final ThreadLocal<PlayerEntity> currentPlayer = new ThreadLocal<>();

	@Inject(method = "onPlayerCollision", at = @At("HEAD"))
	private void capturePlayer(PlayerEntity player, CallbackInfo ci) {
		currentPlayer.set(player);
	}
	
	@Inject(method = "onPlayerCollision", at = @At("RETURN"))
	private void clearPlayer(PlayerEntity player, CallbackInfo ci) {
		currentPlayer.remove();
	}
	
	@Shadow
	public int pickingCount = 1;

	@Inject(at = @At("HEAD"), method = "onPlayerCollision", cancellable = true)
	public void cardboard$check_PlayerPickupExperienceEvent(PlayerEntity player, CallbackInfo ci) {
		if (player instanceof ServerPlayerEntity serverPlayer
				&& player.experiencePickUpDelay == 0
				&& new PlayerPickupExperienceEvent(
						(Player) ((IMixinServerEntityPlayer)serverPlayer).getBukkitEntity(),
						(ExperienceOrb) ((IMixinEntity) ((ExperienceOrbEntity) (Object) this)).getBukkitEntity()
					).callEvent()) {
			// Continue
		} else {
			ci.cancel();
			return;
		}
	}

	@ModifyVariable(
			method = "onPlayerCollision",
			at = @At(
					value = "STORE"
					),
			ordinal = 0
			)
	private int cardboard$modifyPickupDelay_callPlayerXpCooldownEvent(int original) {
		PlayerEntity player = currentPlayer.get();
		return CraftEventFactory.callPlayerXpCooldownEvent(player, original, ChangeReason.PICKUP_ORB).getNewCooldown();
	}

	@ModifyArg(
			method = "onPlayerCollision",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/player/PlayerEntity;addExperience(I)V"
					),
			index = 0
			)
	private int modifyExperienceAmount(int original) {
		PlayerEntity player = currentPlayer.get();
		if (player == null) return original;

		return CraftEventFactory.callPlayerExpChangeEvent(
				player,
				(ExperienceOrbEntity)(Object)this,
				original
				).getAmount();
	}

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"), method = "repairPlayerGears")
    public int doBukkitEvent_PlayerItemMendEvent(int a, int b, ServerPlayerEntity entityhuman) {
        
        Optional<EnchantmentEffectContext> optional = EnchantmentHelper.chooseEquipmentWith(EnchantmentEffectComponentTypes.REPAIR_WITH_XP, entityhuman, ItemStack::isDamaged);

        ItemStack itemstack = optional.get().stack();
        EquipmentSlot slot = optional.get().slot();

        int i = Math.min(a, b);
        PlayerItemMendEvent event = CraftEventFactory.callPlayerItemMendEvent(entityhuman, (ExperienceOrbEntity)(Object)this, itemstack, i);
        i = event.getRepairAmount();
        if (!event.isCancelled()) {
            return i;
        } else return 0;
    }

    /*
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;repairPlayerGears(Lnet/minecraft/server/network/ServerPlayerEntity;I)I"), method = "onPlayerCollision")
    public int doBukkitEvent_PlayerExpChangeEvent(ExperienceOrbEntity e, ServerPlayerEntity plr, int a) {
        return repairPlayerGears((ServerPlayerEntity) plr, CraftEventFactory.callPlayerExpChangeEvent(plr, (ExperienceOrbEntity)(Object)this).getAmount());
    }
    */

    @Shadow
    private int repairPlayerGears(ServerPlayerEntity player, int amount) {
        return 0;
    }
    

}
package org.cardboardpowered.mixin.entity;

import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.event.player.PlayerItemMendEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.javazilla.bukkitfabric.impl.BukkitEventFactory;

import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ExperienceOrbEntity.class)
public class MixinExperienceOrbEntity extends MixinEntity {

    @Shadow
    public int amount;

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"), method = "repairPlayerGears")
    public int doBukkitEvent_PlayerItemMendEvent(int a, int b, ServerPlayerEntity entityhuman) {
        
        Optional<EnchantmentEffectContext> optional = EnchantmentHelper.chooseEquipmentWith(EnchantmentEffectComponentTypes.REPAIR_WITH_XP, entityhuman, ItemStack::isDamaged);

        ItemStack itemstack = optional.get().stack();
        EquipmentSlot slot = optional.get().slot();

        int i = Math.min(a, b);
        PlayerItemMendEvent event = BukkitEventFactory.callPlayerItemMendEvent(entityhuman, (ExperienceOrbEntity)(Object)this, itemstack, i);
        i = event.getRepairAmount();
        if (!event.isCancelled()) {
            return i;
        } else return 0;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;repairPlayerGears(Lnet/minecraft/server/network/ServerPlayerEntity;I)I"), method = "onPlayerCollision")
    public int doBukkitEvent_PlayerExpChangeEvent(ExperienceOrbEntity e, ServerPlayerEntity plr, int a) {
        return repairPlayerGears((ServerPlayerEntity) plr, BukkitEventFactory.callPlayerExpChangeEvent(plr, this.amount).getAmount());
    }

    @Shadow
    private int repairPlayerGears(ServerPlayerEntity player, int amount) {
        return 0;
    }

}
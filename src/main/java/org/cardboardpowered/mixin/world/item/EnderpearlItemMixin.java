package org.cardboardpowered.mixin.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.cardboardpowered.bridge.server.level.ServerPlayerBridge;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.cardboardpowered.bridge.world.entity.EntityBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(value = EnderpearlItem.class, priority = 5000)
public class EnderpearlItemMixin extends Item {

    public EnderpearlItemMixin(net.minecraft.world.item.Item.Properties settings) {
        super(settings);
    }

    /**
     * @author
     * @reason
     */
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void cardboard$use(
            net.minecraft.world.level.Level world,
            net.minecraft.world.entity.player.Player user,
            net.minecraft.world.InteractionHand hand,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<net.minecraft.world.InteractionResult> cir
    ) {
        ItemStack itemStack = user.getItemInHand(hand);

        if (world.isClientSide()) {
            return;
        }

        ThrownEnderpearl enderPearlEntity =
                new ThrownEnderpearl(world, user, new ItemStack(Items.ENDER_PEARL));

        enderPearlEntity.setItem(itemStack);
        enderPearlEntity.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 1.5F, 1.0F);

        EntityShootBowEvent event =
                CraftEventFactory.callEntityShootBowEvent(
                        user,
                        itemStack,
                        itemStack,
                        enderPearlEntity,
                        hand,
                        1.5F,
                        true
                );

        if (event.isCancelled()) {
            event.getProjectile().remove();
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        if (!world.addFreshEntity(enderPearlEntity)) {
            if (user instanceof ServerPlayerBridge serverPlayerBridge) {
                org.bukkit.entity.Player bukkitPlayer =
                        (org.bukkit.entity.Player) ((EntityBridge) serverPlayerBridge).getBukkitEntity();

                bukkitPlayer.updateInventory();
            }
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        world.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                SoundEvents.ENDER_PEARL_THROW,
                SoundSource.NEUTRAL,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        user.awardStat(Stats.ITEM_USED.get(Items.ENDER_PEARL));

        if (!user.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}

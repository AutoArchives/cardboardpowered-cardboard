package org.cardboardpowered.mixin.item;

import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.util.ActionResult;

@Mixin(value = EnderPearlItem.class, priority = 900)
public class MixinEnderPearlItem extends Item {

    public MixinEnderPearlItem(net.minecraft.item.Item.Settings settings) {
        super(settings);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!world.isClient()) {
            EnderPearlEntity enderPearlEntity = new EnderPearlEntity(world, user, new ItemStack(Items.ENDER_PEARL));
            enderPearlEntity.setItem(itemStack);
            enderPearlEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
            if (!world.spawnEntity(enderPearlEntity)) {
                if (user instanceof IMixinServerEntityPlayer) {
                    ((IMixinServerEntityPlayer) user).getBukkit().updateInventory();
                }
                return ActionResult.FAIL;
                // return TypedActionResult.fail(itemStack);
            }
        }

        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        // user.getItemCooldownManager().set((EnderPearlItem)(Object) this, 20);

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        // return TypedActionResult.success(itemStack, world.isClient());
        return ActionResult.SUCCESS;
    }
}

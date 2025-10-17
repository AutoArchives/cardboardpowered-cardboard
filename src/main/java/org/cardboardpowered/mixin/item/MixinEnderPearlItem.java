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
import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
// import net.minecraft.world.entity.projectile.Projectile;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

@Mixin(value = EnderPearlItem.class, priority = 900)
public class MixinEnderPearlItem extends Item {

    public MixinEnderPearlItem(net.minecraft.item.Item.Settings settings) {
        super(settings);
    }

    // @Override
    /*
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemstack = user.getStackInHand(hand);
        if (world instanceof ServerWorld) {
            ServerWorld worldserver = (ServerWorld)world;
            Projectile.Delayed<EnderPearlEntity> thrownEnderpearl = ProjectileEntity.spawnProjectileFromRotationDelayed(EnderPearlEntity::new, worldserver, itemstack, user, 0.0f, POWER, 1.0f);
            PlayerLaunchProjectileEvent event = new PlayerLaunchProjectileEvent((Player)user.getBukkitEntity(), (org.bukkit.inventory.ItemStack)CraftItemStack.asCraftMirror(itemstack), (Projectile)thrownEnderpearl.projectile().getBukkitEntity());
            if (event.callEvent() && thrownEnderpearl.attemptSpawn()) {
                if (event.shouldConsume()) {
                    itemstack.decrementUnlessCreative(1, user);
                } else if (user instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity)user).getBukkitEntity().updateInventory();
                }
                world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
                user.incrementStat(Stats.USED.getOrCreateStat(this));
            } else {
                if (user instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity)user).getBukkitEntity().updateInventory();
                }
                return ActionResult.FAIL;
            }
        }
        world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        return ActionResult.SUCCESS;
    }
    */
    
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

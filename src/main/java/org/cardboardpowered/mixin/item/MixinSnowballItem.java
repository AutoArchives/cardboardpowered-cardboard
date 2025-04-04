package org.cardboardpowered.mixin.item;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SnowballItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = SnowballItem.class, priority = 900)
public class MixinSnowballItem extends Item {

    public MixinSnowballItem(net.minecraft.item.Item.Settings settings) {
        super(settings);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!world.isClient) {
            SnowballEntity snowballEntity = new SnowballEntity(world, user, itemStack);
            snowballEntity.setItem(itemStack);
            snowballEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
            if (!world.spawnEntity(snowballEntity)) {
                if (user instanceof IMixinServerEntityPlayer) {
                    ((IMixinServerEntityPlayer) user).getBukkit().updateInventory();
                }
                return ActionResult.FAIL;
            }
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }
        return ActionResult.SUCCESS;
    }
    
    /*
    private static float cb$POWER = 1.5f;
    
    // @Override
    public ActionResult use_new(World world, PlayerEntity user, Hand hand) {
        ItemStack itemstack = user.getStackInHand(hand);
        if (world instanceof ServerWorld) {
        	
            ServerWorld worldserver = (ServerWorld)world;
            Projectile.Delayed<SnowballEntity> snowball = ProjectileEntity.spawnProjectileFromRotationDelayed(SnowballEntity::new, worldserver, itemstack, user, 0.0f, cb$POWER, 1.0f);
            PlayerLaunchProjectileEvent event = new PlayerLaunchProjectileEvent((Player)user.getBukkitEntity(), (org.bukkit.inventory.ItemStack)CraftItemStack.asCraftMirror(itemstack), (Projectile)snowball.projectile().getBukkitEntity());
            if (event.callEvent() && snowball.attemptSpawn()) {
                user.incrementStat(Stats.USED.getOrCreateStat(this));
                if (event.shouldConsume()) {
                    itemstack.decrementUnlessCreative(1, user);
                } else if (user instanceof ServerPlayerEntity) {
                	((IMixinServerEntityPlayer) user).getBukkit().updateInventory();
                }
                world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            } else {
                if (user instanceof ServerPlayerEntity) {
                	((IMixinServerEntityPlayer) user).getBukkit().updateInventory();
                }
                return ActionResult.FAIL;
            }
        }
        return ActionResult.SUCCESS;
    }
    */
    
}

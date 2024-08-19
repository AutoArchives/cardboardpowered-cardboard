package org.cardboardpowered.mixin.item;

// import java.util.Random;

import com.javazilla.bukkitfabric.impl.BukkitEventFactory;
import com.javazilla.bukkitfabric.interfaces.IMixinServerEntityPlayer;
import com.javazilla.bukkitfabric.interfaces.IMixinWorld;

import me.isaiah.common.cmixin.IMixinItemStack;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.cardboardpowered.interfaces.IItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(value = ItemStack.class, priority = 999)
public class MixinItemStack implements IItemStack {

    @Shadow
    private Item item;
    
    @Shadow
    private int count;
    
    @Shadow
    private ComponentMapImpl components;

    @Override
    public void cardboard$restore_patch(ComponentChanges changes) {
        this.components.setChanges(changes);
    }
    
    private CraftItemStack bukkitStack;
    
    @Override
    public org.bukkit.inventory.ItemStack getBukkitStack() {
        if (this.bukkitStack == null || this.bukkitStack.handle != ((ItemStack)(Object)this)) {
            this.bukkitStack = CraftItemStack.asCraftMirror( ((ItemStack)(Object)this) );
        }
        return this.bukkitStack;
    }
    
    @Override
    public void cb$setItem(Item item) {
        this.bukkitStack = null;
        this.item = item;
        ComponentChanges patch = ((ItemStack)(Object)this).getComponentChanges();
        this.components = new ComponentMapImpl(this.item.getComponents());
        ((ItemStack)(Object)this).applyUnvalidatedChanges(patch);
    }

    
    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/lang/Runnable;)V", cancellable = true)
    public void callPlayerItemDamageEvent(int i, Random random, ServerPlayerEntity player, Runnable cb, CallbackInfo ci) {
        if (!((ItemStack)(Object)this).isDamageable()) {
            //ci.setReturnValue(false);
            ci.cancel();
            return;
        }
        int j;

        if (i > 0) {
            j = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, ((ItemStack)(Object)this));
            for (int l = 0; j > 0 && l < i; ++l) if (UnbreakingEnchantment.shouldPreventDamage(((ItemStack)(Object)this), j, random)) i--;

            if (player != null) {
                PlayerItemDamageEvent event = new PlayerItemDamageEvent((Player) ((IMixinServerEntityPlayer)player).getBukkitEntity(), CraftItemStack.asCraftMirror((ItemStack)(Object)this), i);
                event.getPlayer().getServer().getPluginManager().callEvent(event);

                if (i != event.getDamage() || event.isCancelled()) event.getPlayer().updateInventory();
                if (event.isCancelled()) {
                    //ci.setReturnValue(false);
                	ci.cancel();
                	return;
                }
                i = event.getDamage();
            }
            if (i <= 0) {
                //ci.setReturnValue(false);
            	ci.cancel();
            	return;
            }
        }
        if (player != null && i != 0) {
        	Criteria.ITEM_DURABILITY_CHANGED.trigger(player, ((ItemStack)(Object)this), ((ItemStack)(Object)this).getDamage() + i);
        }

        j = ((ItemStack)(Object)this).getDamage() + i;
        ((ItemStack)(Object)this).setDamage(j);
        //ci.setReturnValue(j >= ((ItemStack)(Object)this).getMaxDamage());
        
        if (j >= ((ItemStack)(Object)this).getMaxDamage()) {
        	cb.run();
        }
        
        ci.cancel();
        
    }

    /**
     * @author cardboard
     * @reason BlockPlaceEvent
     */
    @Overwrite
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        BlockPos blockPos = context.getBlockPos();
        CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(context.getWorld(), blockPos, false);
        if (playerEntity != null && !playerEntity.abilities.allowModifyWorld
                // FIXME: 1.18.2: Adventure mode place test.
                /*&& !((ItemStack)(Object)this).canPlaceOn(context.getWorld().getTagManager(), cachedBlockPosition)*/) {
            return ActionResult.PASS;
        }
        ((IMixinWorld)context.getWorld()).setCaptureBlockStates_BF(true);

        Item item = ((ItemStack)(Object)this).getItem();
        ActionResult actionResult = item.useOnBlock(context);

        if (actionResult != ActionResult.FAIL) {
            if (((IMixinWorld)context.getWorld()).getCapturedBlockStates_BF().size() > 0) {
                List<BlockState> blocks = new java.util.ArrayList<>(((IMixinWorld)context.getWorld()).getCapturedBlockStates_BF().values());
                ((IMixinWorld)context.getWorld()).getCapturedBlockStates_BF().clear();
                BlockPlaceEvent placeEvent = BukkitEventFactory.callBlockPlaceEvent((ServerWorld)context.getWorld(), playerEntity, Hand.MAIN_HAND, blocks.get(0), blockPos.getX(), blockPos.getY(), blockPos.getZ()); 
                if ((placeEvent.isCancelled() || !placeEvent.canBuild())) {
                    ((IMixinWorld)context.getWorld()).setCaptureBlockStates_BF(false);
    
                    CraftBlockState b = (CraftBlockState) blocks.get(0);
                    BlockPos pos = b.getPosition();
                    while (context.getWorld().getBlockState(pos) != Blocks.AIR.getDefaultState())
                        context.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
    
                    context.getStack().increment(1);
                    ((Player)((IMixinServerEntityPlayer)context.getPlayer()).getBukkitEntity()).updateInventory();
                    return ActionResult.FAIL;
                }
            }
        }

        if (playerEntity != null && actionResult.isAccepted()) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(item));
        }
        ((IMixinWorld)context.getWorld()).setCaptureBlockStates_BF(false);
        return actionResult;
    }

    @Inject(
    		method = "method_56097",
    		at = @At(
    				value = "INVOKE",
    				target = "Lnet/minecraft/item/ItemStack;decrement(I)V"
    			)
    	)
    private <T extends LivingEntity> void cardboard$call_player_item_break_event(LivingEntity entityIn, EquipmentSlot equipmentSlot, CallbackInfo ci) {
        if (this.count == 1 && entityIn instanceof PlayerEntity) {
        	BukkitEventFactory.callPlayerItemBreakEvent((PlayerEntity) entityIn, ((ItemStack)(Object)this));
        }
    }

    /*@Inject(at = @At("HEAD"), method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", cancellable = true)
    public <T extends LivingEntity> void damage(int i, T t0, Consumer<T> consumer, CallbackInfo ci) {
    	

        if (!t0.getWorld().isClient && (!(t0 instanceof PlayerEntity) || !((PlayerEntity) t0).abilities.creativeMode)) {
            if (((ItemStack)(Object)this).isDamageable()) {
            	
                if (((ItemStack)(Object)this).damage(i, t0.getRandom(), t0 instanceof ServerPlayerEntity ? (ServerPlayerEntity) t0 : null)) {
                    consumer.accept(t0);
                    Item item = ((ItemStack)(Object)this).getItem();
                    if (((ItemStack)(Object)this).count == 1 && t0 instanceof PlayerEntity)
                        BukkitEventFactory.callPlayerItemBreakEvent((PlayerEntity) t0, ((ItemStack)(Object)this));

                    ((ItemStack)(Object)this).decrement(1);
                    if (t0 instanceof PlayerEntity)
                        ((PlayerEntity) t0).incrementStat(Stats.BROKEN.getOrCreateStat(item));
                    ((ItemStack)(Object)this).setDamage(0);
                }

            }
        }
        ci.cancel();
        return;
    }*/

}

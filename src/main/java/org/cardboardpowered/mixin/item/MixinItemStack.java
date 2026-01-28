package org.cardboardpowered.mixin.item;

import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.cardboardpowered.bridge.server.level.ServerPlayerBridge;
import org.cardboardpowered.bridge.world.level.LevelBridge;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.cardboardpowered.bridge.world.item.ItemStackBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

@Mixin(value = ItemStack.class, priority = 999)
public class MixinItemStack implements ItemStackBridge {

    @Shadow
    private Item item;
    
    @Shadow
    private int count;
    
    @Shadow
    private PatchedDataComponentMap components;

    @Override
    public void cardboard$restore_patch(DataComponentPatch changes) {
        this.components.restorePatch(changes);
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
        DataComponentPatch patch = ((ItemStack)(Object)this).getComponentsPatch();
        this.components = new PatchedDataComponentMap(this.item.components());
        ((ItemStack)(Object)this).applyComponents(patch);
    }

    @Inject(at = @At("HEAD"), method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V", cancellable = true)
    public void callPlayerItemDamageEvent( int i, ServerLevel world, ServerPlayer player, java.util.function.Consumer<Item> breakCallback, CallbackInfo ci) {
        if (!((ItemStack)(Object)this).isDamageableItem()) {
            //ci.setReturnValue(false);
            ci.cancel();
            return;
        }
        int j;

        if (i > 0) {
        	// j = EnchantmentHelper.getItemDamage(player.getWorld(), ((ItemStack)(Object)this), i);
        	// j = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, ((ItemStack)(Object)this));
            // for (int l = 0; j > 0 && l < i; ++l) if (UnbreakingEnchantment.shouldPreventDamage(((ItemStack)(Object)this), j, random)) i--;

            if (player != null) {
                j = EnchantmentHelper.processDurabilityChange(player.level(), ((ItemStack)(Object)this), i);
                PlayerItemDamageEvent event = new PlayerItemDamageEvent((Player) ((ServerPlayerBridge)player).getBukkitEntity(), CraftItemStack.asCraftMirror((ItemStack)(Object)this), i, j);
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
        	CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(player, ((ItemStack)(Object)this), ((ItemStack)(Object)this).getDamageValue() + i);
        }

        j = ((ItemStack)(Object)this).getDamageValue() + i;
        ((ItemStack)(Object)this).setDamageValue(j);
        //ci.setReturnValue(j >= ((ItemStack)(Object)this).getMaxDamage());
        
        if (j >= ((ItemStack)(Object)this).getMaxDamage()) {
        	// cb.run();
        	
        	Item item = ((ItemStack)(Object)this).getItem();
            if (this.count == 1 && player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                CraftEventFactory.callPlayerItemBreakEvent(serverPlayer, ((ItemStack)(Object)this));
            }
            ((ItemStack)(Object)this).shrink(1);
            breakCallback.accept(item);
        	
        }
        
        ci.cancel();
        
    }

    /**
     * @author cardboard
     * @reason BlockPlaceEvent
     */
    @Overwrite
    public InteractionResult useOn(UseOnContext context) {
        net.minecraft.world.entity.player.Player playerEntity = context.getPlayer();
        BlockPos blockPos = context.getClickedPos();
        BlockInWorld cachedBlockPosition = new BlockInWorld(context.getLevel(), blockPos, false);
        if (playerEntity != null && !playerEntity.abilities.mayBuild
                // FIXME: 1.18.2: Adventure mode place test.
                /*&& !((ItemStack)(Object)this).canPlaceOn(context.getWorld().getTagManager(), cachedBlockPosition)*/) {
            return InteractionResult.PASS;
        }
        ((LevelBridge)context.getLevel()).setCaptureBlockStates_BF(true);

        Item item = ((ItemStack)(Object)this).getItem();
        InteractionResult actionResult = item.useOn(context);

        if (actionResult != InteractionResult.FAIL) {
            if (((LevelBridge)context.getLevel()).getCapturedBlockStates_BF().size() > 0) {
                List<BlockState> blocks = new java.util.ArrayList<>(((LevelBridge)context.getLevel()).getCapturedBlockStates_BF().values());
                ((LevelBridge)context.getLevel()).getCapturedBlockStates_BF().clear();
                BlockPlaceEvent placeEvent = CraftEventFactory.callBlockPlaceEvent((ServerLevel)context.getLevel(), playerEntity, InteractionHand.MAIN_HAND, blocks.get(0), blockPos.getX(), blockPos.getY(), blockPos.getZ()); 
                if ((placeEvent.isCancelled() || !placeEvent.canBuild())) {
                    ((LevelBridge)context.getLevel()).setCaptureBlockStates_BF(false);
    
                    CraftBlockState b = (CraftBlockState) blocks.get(0);
                    BlockPos pos = b.getPosition();
                    while (context.getLevel().getBlockState(pos) != Blocks.AIR.defaultBlockState())
                        context.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    
                    context.getItemInHand().grow(1);
                    ((Player)((ServerPlayerBridge)context.getPlayer()).getBukkitEntity()).updateInventory();
                    return InteractionResult.FAIL;
                }
            }
        }

        if (playerEntity != null && actionResult.consumesAction()) {
            playerEntity.awardStat(Stats.ITEM_USED.get(item));
        }
        ((LevelBridge)context.getLevel()).setCaptureBlockStates_BF(false);
        return actionResult;
    }
    
    @Inject(
    		method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V",
    		require = 0,
    		at = @At(
    				value = "INVOKE",
    				target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"
    		)
    )
    private void arclight$itemBreak(int amount, ServerLevel level, ServerPlayer serverPlayer, Consumer<Item> onBroken, CallbackInfo ci) {
        if (this.count == 1 && serverPlayer != null) {
        	CraftEventFactory.callPlayerItemBreakEvent(serverPlayer, (ItemStack) (Object) this);
        }
    }

	@Override
	public org.bukkit.inventory.ItemStack asBukkitCopy() {
        return CraftItemStack.asCraftMirror( ((ItemStack)(Object)this).copy());
    }
	
    /*
    @Inject(
    		method = "method_56097",
    		at = @At(
    				value = "INVOKE",
    				target = "Lnet/minecraft/item/ItemStack;decrement(I)V"
    			)
    	)
    private <T extends LivingEntity> void cardboard$call_player_item_break_event(LivingEntity entityIn, EquipmentSlot equipmentSlot, Item item, CallbackInfo ci) {
        if (this.count == 1 && entityIn instanceof PlayerEntity) {
        	CraftEventFactory.callPlayerItemBreakEvent((PlayerEntity) entityIn, ((ItemStack)(Object)this));
      }
    }
    */

    /*@Inject(at = @At("HEAD"), method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", cancellable = true)
    public <T extends LivingEntity> void damage(int i, T t0, Consumer<T> consumer, CallbackInfo ci) {
    	

        if (!t0.getWorld().isClient && (!(t0 instanceof PlayerEntity) || !((PlayerEntity) t0).abilities.creativeMode)) {
            if (((ItemStack)(Object)this).isDamageable()) {
            	
                if (((ItemStack)(Object)this).damage(i, t0.getRandom(), t0 instanceof ServerPlayerEntity ? (ServerPlayerEntity) t0 : null)) {
                    consumer.accept(t0);
                    Item item = ((ItemStack)(Object)this).getItem();
                    if (((ItemStack)(Object)this).count == 1 && t0 instanceof PlayerEntity)
                        CraftEventFactory.callPlayerItemBreakEvent((PlayerEntity) t0, ((ItemStack)(Object)this));

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

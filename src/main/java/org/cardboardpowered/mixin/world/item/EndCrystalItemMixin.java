package org.cardboardpowered.mixin.world.item;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EndCrystalItem.class, priority = 900)
public class EndCrystalItemMixin {

    /**
     * @reason .
     * @author .
     */
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void cardboard$useOnBlock(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {

        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState state = world.getBlockState(blockpos);

        if (!state.is(Blocks.BEDROCK) && !state.is(Blocks.OBSIDIAN)) {
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        BlockPos above = blockpos.above();

        if (!world.isEmptyBlock(above)) {
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        double x = above.getX() + 0.5D;
        double y = above.getY();
        double z = above.getZ() + 0.5D;

        AABB box = new AABB(x - 0.5D, y, z - 0.5D, x + 0.5D, y + 2.0D, z + 0.5D);

        if (!world.getEntities((Entity) null, box).isEmpty()) {
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        if (world instanceof ServerLevel serverLevel) {

            EndCrystal crystal = new EndCrystal(serverLevel, x, y, z);
            crystal.setShowBottom(false);

            var event = CraftEventFactory.callEntityPlaceEvent(context, crystal);
            if (event.isCancelled()) {
                cir.setReturnValue(InteractionResult.FAIL);
                return;
            }

            serverLevel.addFreshEntity(crystal);

            var fight = serverLevel.getDragonFight();
            if (fight != null) {
                fight.tryRespawn();
            }
        }

        context.getItemInHand().shrink(1);

        cir.setReturnValue(InteractionResult.SUCCESS);
    }

}
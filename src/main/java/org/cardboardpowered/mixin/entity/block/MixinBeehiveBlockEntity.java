package org.cardboardpowered.mixin.entity.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cardboardpowered.interfaces.IMixinEntity;

@Mixin(BeehiveBlockEntity.class)
public class MixinBeehiveBlockEntity extends BlockEntity {

    public MixinBeehiveBlockEntity( BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    // NOTE: 1.20.6: Removed ZI argument
    
    // NOTE: 1.21.4: tryEnterHive(Entity) -> tryEnterHive(BeeEntity)

    
    // Lnet/minecraft/block/entity/BeehiveBlockEntity;tryEnterHive(Lnet/minecraft/entity/passive/BeeEntity;)V
    
    // TODO: 1.21.4
    
    /*
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;stopRiding()V"), cancellable = true,
            method = "Lnet/minecraft/block/entity/BeehiveBlockEntity;tryEnterHive(Lnet/minecraft/entity/passive/BeeEntity;)V")
    public void bukkitize_tryEnterHive(BeeEntity entity, CallbackInfo ci) {
        if (this.world != null) {
            org.bukkit.event.entity.EntityEnterBlockEvent event = new org.bukkit.event.entity.EntityEnterBlockEvent(((IMixinEntity)entity).getBukkitEntity(), CraftBlock.at((ServerWorld) world, getPos()));
            org.bukkit.Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                entity.setCannotEnterHiveTicks(400);
                ci.cancel();
                return;
            }
        }
    }
    */

}

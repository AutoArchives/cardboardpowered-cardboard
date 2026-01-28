package org.cardboardpowered.mixin.entity.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.bukkit.Location;
import org.spongepowered.asm.mixin.Mixin;

import org.cardboardpowered.interfaces.IMixinLockableContainerBlockEntity;
import org.cardboardpowered.bridge.world.level.LevelBridge;

@Mixin(BaseContainerBlockEntity.class)
public class MixinLockableContainerBlockEntity implements IMixinLockableContainerBlockEntity {

    @Override
    public Location getLocation() {
        BaseContainerBlockEntity lc = (BaseContainerBlockEntity)(Object)this;
        BlockPos pos = lc.getBlockPos();
        return new Location(((LevelBridge)lc.level).getCraftWorld(), pos.getX(), pos.getY(), pos.getZ());
    }

}
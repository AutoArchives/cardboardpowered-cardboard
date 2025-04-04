package org.cardboardpowered.mixin.block;

import org.bukkit.craftbukkit.event.CraftEventFactory;
import net.minecraft.block.BambooShootBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BambooShootBlock.class)
public class MixinBambooSaplingBlock {

    public boolean bukkitSpreadEvent(World world, BlockPos pos, BlockState newState, int flags) {
        return CraftEventFactory.handleBlockSpreadEvent(world, pos.down(), pos, newState, flags);
    }
}

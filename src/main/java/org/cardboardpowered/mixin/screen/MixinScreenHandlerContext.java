package org.cardboardpowered.mixin.screen;

import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import org.cardboardpowered.bridge.world.inventory.ContainerLevelAccessBridge;
import org.cardboardpowered.bridge.world.level.LevelBridge;

@Mixin(ContainerLevelAccess.class)
public interface MixinScreenHandlerContext extends ContainerLevelAccessBridge {

    @Override
    default Level getWorld() {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    default BlockPos getPosition() {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    default org.bukkit.Location getLocation() {
        return new org.bukkit.Location(((LevelBridge)getWorld()).getCraftWorld(), getPosition().getX(), getPosition().getY(), getPosition().getZ());
    }

    /**
     * @reason Add new methods
     * @author BukkitFabric
     */
    @Overwrite
    static ContainerLevelAccess create(final Level world, final BlockPos blockposition) {
        return new ContainerLevelAccess() {

            @SuppressWarnings("unused")
            public Level getWorld() {
                return world;
            }

            @SuppressWarnings("unused")
            public BlockPos getPosition() {
                return blockposition;
            }

            @Override
            public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> bifunction) {
                return Optional.of(bifunction.apply(world, blockposition));
            }
        };
    }

}
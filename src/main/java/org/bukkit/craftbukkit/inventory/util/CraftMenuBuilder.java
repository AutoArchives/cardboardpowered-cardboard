package org.bukkit.craftbukkit.inventory.util;

import com.javazilla.bukkitfabric.interfaces.IMixinServerEntityPlayer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface CraftMenuBuilder {

    public ScreenHandler build(ServerPlayerEntity var1, ScreenHandlerType<?> var2);

    public static CraftMenuBuilder worldAccess(LocationBoundContainerBuilder builder) {
        return (player, type) -> builder.build(((IMixinServerEntityPlayer)player).nextContainerCounter(), player.getInventory(), ScreenHandlerContext.create(player.getWorld(), player.getBlockPos()));
    }

    public static CraftMenuBuilder tileEntity(TileEntityObjectBuilder objectBuilder, Block block) {
        return (player, type) -> objectBuilder.build(player.getBlockPos(), block.getDefaultState()).createMenu(((IMixinServerEntityPlayer)player).nextContainerCounter(), player.getInventory(), player);
    }

    public static interface LocationBoundContainerBuilder {
        public ScreenHandler build(int var1, PlayerInventory var2, ScreenHandlerContext var3);
    }

    public static interface TileEntityObjectBuilder {
        public NamedScreenHandlerFactory build(BlockPos var1, BlockState var2);
    }

}
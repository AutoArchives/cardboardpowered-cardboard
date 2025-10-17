package org.bukkit.craftbukkit.inventory.view.builder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;
import org.jspecify.annotations.Nullable;

import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;

public class CraftBlockEntityInventoryViewBuilder<V extends InventoryView> extends CraftAbstractLocationInventoryViewBuilder<V> {

    private final Block block;
    private final @Nullable CraftTileInventoryBuilder builder;

    public CraftBlockEntityInventoryViewBuilder(final ScreenHandlerType<?> handle, final Block block, final @Nullable CraftTileInventoryBuilder builder) {
        super(handle);
        this.block = block;
        this.builder = builder;
    }

    @Override
    protected ScreenHandler buildContainer(final ServerPlayerEntity player) {
        if (this.world == null) {
            this.world = player.getEntityWorld();
        }

        if (this.position == null) {
            this.position = player.getBlockPos();
        }

        final BlockEntity entity = this.world.getBlockEntity(position);
        if (!(entity instanceof final ScreenHandlerFactory container)) {
            return buildFakeTile(player);
        }

        final ScreenHandler atBlock = container.createMenu(((IMixinServerEntityPlayer)player).nextContainerCounter(), player.getInventory(), player);
        if (atBlock.getType() != super.handle) {
            return buildFakeTile(player);
        }

        return atBlock;
    }

    private ScreenHandler buildFakeTile(final ServerPlayerEntity player) {
        if (this.builder == null) {
            return handle.create(((IMixinServerEntityPlayer)player).nextContainerCounter(), player.getInventory());
        }
        final NamedScreenHandlerFactory inventory = this.builder.build(this.position, this.block.getDefaultState());
        if (inventory instanceof final BlockEntity tile) {
            tile.setWorld(this.world);
        }
        return inventory.createMenu(((IMixinServerEntityPlayer)player).nextContainerCounter(), player.getInventory(), player);
    }

    @Override
    public LocationInventoryViewBuilder<V> copy() {
        final CraftBlockEntityInventoryViewBuilder<V> copy = new CraftBlockEntityInventoryViewBuilder<>(super.handle, this.block, this.builder);
        copy.world = this.world;
        copy.position = this.position;
        copy.checkReachable = super.checkReachable;
        copy.title = title;
        return copy;
    }

    public interface CraftTileInventoryBuilder {
        NamedScreenHandlerFactory build(BlockPos blockPosition, BlockState blockData);
    }
}

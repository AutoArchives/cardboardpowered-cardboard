package org.bukkit.craftbukkit.inventory.view.builder;

import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;

import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;

public class CraftDoubleChestInventoryViewBuilder<V extends InventoryView> extends CraftAbstractLocationInventoryViewBuilder<V> {

    public CraftDoubleChestInventoryViewBuilder(final ScreenHandlerType<?> handle) {
        super(handle);
    }

    @Override
    protected ScreenHandler buildContainer(final ServerPlayerEntity player) {
        if (super.world == null) {
            return handle.create( ((IMixinServerEntityPlayer)player).nextContainerCounter(), player.getInventory());
        }

        final ChestBlock chest = (ChestBlock) Blocks.CHEST;
        final DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> result = chest.getBlockEntitySource(super.world.getBlockState(super.position), super.world, super.position, false);
        if (result instanceof DoubleBlockProperties.PropertySource.Single<? extends ChestBlockEntity>) {
            return handle.create(((IMixinServerEntityPlayer)player).nextContainerCounter(), player.getInventory());
        }

        final NamedScreenHandlerFactory combined = result.apply(ChestBlock.NAME_RETRIEVER).orElse(null);
        if (combined == null) {
            return handle.create(((IMixinServerEntityPlayer)player).nextContainerCounter(), player.getInventory());
        }
        return combined.createMenu(((IMixinServerEntityPlayer)player).nextContainerCounter(), player.getInventory(), player);
    }

    @Override
    public LocationInventoryViewBuilder<V> copy() {
        final CraftDoubleChestInventoryViewBuilder<V> copy = new CraftDoubleChestInventoryViewBuilder<>(super.handle);
        copy.world = this.world;
        copy.position = this.position;
        copy.checkReachable = super.checkReachable;
        copy.title = title;
        return copy;
    }
}

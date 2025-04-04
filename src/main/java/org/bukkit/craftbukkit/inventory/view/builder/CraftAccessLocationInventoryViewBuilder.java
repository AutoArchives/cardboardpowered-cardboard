package org.bukkit.craftbukkit.inventory.view.builder;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;

import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;

public class CraftAccessLocationInventoryViewBuilder<V extends InventoryView> extends CraftAbstractLocationInventoryViewBuilder<V> {

    private final CraftAccessContainerObjectBuilder containerBuilder;

    public CraftAccessLocationInventoryViewBuilder(final ScreenHandlerType<?> handle, final CraftAccessContainerObjectBuilder containerBuilder) {
        super(handle);
        this.containerBuilder = containerBuilder;
    }

    @Override
    protected ScreenHandler buildContainer(final ServerPlayerEntity player) {
        final ScreenHandlerContext access;
        if (super.position == null) {
            access = ScreenHandlerContext.create(player.getWorld(), player.getBlockPos());
        } else {
            access = ScreenHandlerContext.create(super.world, super.position);
        }

        return this.containerBuilder.build(((IMixinServerEntityPlayer)player).nextContainerCounter(), player.getInventory(), access);
    }

    @Override
    public LocationInventoryViewBuilder<V> copy() {
        final CraftAccessLocationInventoryViewBuilder<V> copy = new CraftAccessLocationInventoryViewBuilder<>(this.handle, this.containerBuilder);
        copy.world = super.world;
        copy.position = super.position;
        copy.checkReachable = super.checkReachable;
        copy.title = title;
        return copy;
    }

    public interface CraftAccessContainerObjectBuilder {
        ScreenHandler build(final int syncId, final PlayerInventory inventory, ScreenHandlerContext access);
    }
}

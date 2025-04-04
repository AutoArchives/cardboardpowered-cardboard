package org.bukkit.craftbukkit.inventory.view.builder;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.InventoryViewBuilder;

import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;

public class CraftStandardInventoryViewBuilder<V extends InventoryView> extends CraftAbstractInventoryViewBuilder<V> {

    public CraftStandardInventoryViewBuilder(final ScreenHandlerType<?> handle) {
        super(handle);
    }

    @Override
    protected ScreenHandler buildContainer(final ServerPlayerEntity player) {
        return super.handle.create(((IMixinServerEntityPlayer)player).nextContainerCounter(), player.getInventory());
    }

    @Override
    public InventoryViewBuilder<V> copy() {
        final CraftStandardInventoryViewBuilder<V> copy = new CraftStandardInventoryViewBuilder<>(handle);
        copy.title = this.title;
        return copy;
    }
}

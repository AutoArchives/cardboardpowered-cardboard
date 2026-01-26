package org.bukkit.craftbukkit.inventory.view.builder;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.InventoryViewBuilder;

import org.cardboardpowered.bridge.server.level.ServerPlayerBridge;

public class CraftStandardInventoryViewBuilder<V extends InventoryView> extends CraftAbstractInventoryViewBuilder<V> {

    public CraftStandardInventoryViewBuilder(final MenuType<?> handle) {
        super(handle);
    }

    @Override
    protected AbstractContainerMenu buildContainer(final ServerPlayer player) {
        return super.handle.create(((ServerPlayerBridge)player).cardboard$nextContainerCounter(), player.getInventory());
    }

    @Override
    public InventoryViewBuilder<V> copy() {
        final CraftStandardInventoryViewBuilder<V> copy = new CraftStandardInventoryViewBuilder<>(handle);
        copy.title = this.title;
        return copy;
    }
}

package org.bukkit.craftbukkit.inventory.view.builder;

import com.google.common.base.Preconditions;
import org.cardboardpowered.interfaces.IMixinScreenHandler;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.InventoryViewBuilder;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

public abstract class CraftAbstractInventoryViewBuilder<V extends InventoryView> implements InventoryViewBuilder<V> {

    protected final ScreenHandlerType<?> handle;

    protected boolean checkReachable = false;
    protected @MonotonicNonNull Component title = null;

    public CraftAbstractInventoryViewBuilder(final ScreenHandlerType<?> handle) {
        this.handle = handle;
    }

    @Override
    public InventoryViewBuilder<V> title(final Component title) {
        this.title = title;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V build(final HumanEntity player) {
        Preconditions.checkArgument(player != null, "The given player must not be null");
        Preconditions.checkArgument(this.title != null, "The given title must not be null");
        Preconditions.checkArgument(player instanceof CraftHumanEntity, "The given player must be a CraftHumanEntity");
        final CraftHumanEntity craftHuman = (CraftHumanEntity) player;
        Preconditions.checkArgument(craftHuman.getHandle() instanceof ServerPlayerEntity, "The given player must be an EntityPlayer");
        final ServerPlayerEntity serverPlayer = (ServerPlayerEntity) craftHuman.getHandle();
        final ScreenHandler container = buildContainer(serverPlayer);
        
        
        IMixinScreenHandler sh = (IMixinScreenHandler) container;
        sh.setCheckReachable( this.checkReachable );
        sh.setTitle( PaperAdventure.asVanilla(this.title) );
        
        
        // container.checkReachable = this.checkReachable;
        // container.setTitle(PaperAdventure.asVanilla(this.title));
        return (V) ((IMixinScreenHandler)container).getBukkitView();
    }

    protected abstract ScreenHandler buildContainer(ServerPlayerEntity player);
}

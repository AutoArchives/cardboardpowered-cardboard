package org.cardboardpowered.impl.inventory;

import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftContainer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftMenuType;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.cardboardpowered.bridge.world.inventory.AbstractContainerMenuBridge;

// public class CardboardInventoryView extends InventoryView {
public class CardboardInventoryView<T extends AbstractContainerMenu, I extends Inventory> extends CardboardAbstractInventoryView {

    public final AbstractContainerMenu container;
    public CraftHumanEntity player;
    public final I viewing;
    public final String originalTitle;
    public String title;

    public CardboardInventoryView(HumanEntity player, I viewing, AbstractContainerMenu container) {
        this.player = (null !=player) ? (CraftHumanEntity) player : null;
        this.viewing = viewing;
        this.container = container;
        this.title = this.originalTitle = CraftChatMessage.fromComponent(((AbstractContainerMenuBridge)container).getTitle() );
    }

    public void setPlayerIfNotSet(HumanEntity player) {
        if (null == this.player)
            this.player = (CraftHumanEntity) player;
    }

    @Override
    public I getTopInventory() {
        return viewing;
    }

    @Override
    public Inventory getBottomInventory() {
        return player.getInventory();
    }

    @Override
    public HumanEntity getPlayer() {
        return player;
    }

    @Override
    public InventoryType getType() {
        InventoryType type = viewing.getType();
        return (type == InventoryType.CRAFTING && player.getGameMode() == GameMode.CREATIVE) ? InventoryType.CREATIVE : type;
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(item);
        if (slot >= 0) {
            this.container.getSlot(slot).set(stack);
        } else {
            this.player.getHandle().drop(stack, false);
        }
    }

    @Override
    public ItemStack getItem(int slot) {
        return (slot < 0) ? null : CraftItemStack.asCraftMirror(container.getSlot(slot).getItem());
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public boolean isInTop(int rawSlot) {
        return rawSlot < viewing.getSize();
    }

    public AbstractContainerMenu getHandle() {
        return container;
    }

	// @Override
	public @NotNull String getOriginalTitle() {
		return this.originalTitle;
	}

	// @Override
	public void setTitle(@NotNull String arg0) {
		sendInventoryTitleChange(this, title);
        this.title = arg0;
	}
	
    public static void sendInventoryTitleChange(InventoryView view, String title) {
        // Preconditions.checkArgument((view != null ? 1 : 0) != 0, (Object)"InventoryView cannot be null");
        // Preconditions.checkArgument((title != null ? 1 : 0) != 0, (Object)"Title cannot be null");
        // Preconditions.checkArgument((boolean)(view.getPlayer() instanceof Player), (Object)"NPCs are not currently supported for this function");
        // Preconditions.checkArgument((boolean)view.getTopInventory().getType().isCreatable(), (Object)"Only creatable inventories can have their title changed");
        ServerPlayer entityPlayer = (ServerPlayer)((CraftHumanEntity)view.getPlayer()).getHandle();
        int containerId = entityPlayer.containerMenu.containerId;
        net.minecraft.world.inventory.MenuType windowType = CraftContainer.getNotchInventoryType(view.getTopInventory());
        entityPlayer.connection.send(new ClientboundOpenScreenPacket(containerId, windowType, CraftChatMessage.fromString(title)[0]));
        ((Player)view.getPlayer()).updateInventory();
    }

	public I getTopInventorya() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @Nullable MenuType getMenuType() {
		net.minecraft.world.inventory.MenuType<?> menuType = ((AbstractContainerMenu)this.container).getType();
        return menuType != null ? CraftMenuType.minecraftToBukkit(menuType) : null;
	}

}
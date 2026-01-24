package org.bukkit.craftbukkit.inventory;

import org.cardboardpowered.interfaces.IMixinEntity;
import org.cardboardpowered.interfaces.IMixinScreenHandler;

import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import org.cardboardpowered.impl.entity.CraftPlayer;
import org.cardboardpowered.impl.inventory.CardboardAbstractInventoryView;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.cardboardpowered.impl.entity.CraftPlayer;

public class CraftContainer extends AbstractContainerMenu {

    private final InventoryView view;
    private InventoryType cachedType;
    private String cachedTitle;
    private AbstractContainerMenu delegate;
    private final int cachedSize;

    public CraftContainer(InventoryView view, Player player, int id) {
        super(getNotchInventoryType(view.getTopInventory()), id);
        this.view = view;
        net.minecraft.world.Container top = ((CraftInventory) view.getTopInventory()).getInventory();
        net.minecraft.world.entity.player.Inventory bottom = (net.minecraft.world.entity.player.Inventory) ((CraftInventory) view.getBottomInventory()).getInventory();
        cachedType = view.getType();
        cachedTitle = view.getTitle();
        cachedSize = getSize();
        setupSlots(top, bottom, player);
    }

    public CraftContainer(final Inventory inventory, final Player player, int id) {
        this(new CardboardAbstractInventoryView() {
            @Override public Inventory getTopInventory()    { return inventory;  }
            @Override public Inventory getBottomInventory() { return getPlayer().getInventory(); }
            @Override public HumanEntity getPlayer()        { return (HumanEntity)((IMixinEntity)player).getBukkitEntity(); }
            @Override public InventoryType getType()        { return inventory.getType(); }

            private final String originalTitle;
            private String title;
            {
                this.title = this.originalTitle = inventory instanceof CraftInventoryCustom ? ((CraftInventoryCustom.MinecraftInventory) ((CraftInventory) inventory).getInventory()).getTitle() : inventory.getType().getDefaultTitle();
            }

            public Component title() {
                Component component = null;
                if (inventory instanceof CraftInventoryCustom) {
                    CraftInventoryCustom custom = (CraftInventoryCustom)inventory;
                    // TODO component = custom.title();
                } else {
                    component = inventory.getType().defaultTitle();
                }
                return component;
            }

            public String getTitle() {
                return this.title;
            }

            public String getOriginalTitle() {
                return this.originalTitle;
            }

            public void setTitle(String title) {
                CardboardInventoryView.sendInventoryTitleChange(this, title);
                this.title = title;
            }

			@Override
			public @Nullable MenuType getMenuType() {
				return CraftMenuType.minecraftToBukkit(CraftContainer.getNotchInventoryType(inventory));
			}
        }, player, id);
    }

    public CardboardInventoryView getBukkitView() {
        return (CardboardInventoryView) view;
    }

    private int getSize() {
        return view.getTopInventory().getSize();
    }

   // @Override
    public boolean isNotRestricted(Player entityhuman) {
        if (cachedType == view.getType() && cachedSize == getSize() && cachedTitle.equals(view.getTitle())) return true;

        boolean typeChanged = (cachedType != view.getType());
        cachedType = view.getType();
        cachedTitle = view.getTitle();
        if (view.getPlayer() instanceof CraftPlayer) {
            CraftPlayer player = (CraftPlayer) view.getPlayer();
            net.minecraft.world.inventory.MenuType<?> type = getNotchInventoryType(view.getTopInventory());
            net.minecraft.world.Container top = ((CraftInventory) view.getTopInventory()).getInventory();
            net.minecraft.world.entity.player.Inventory bottom = (net.minecraft.world.entity.player.Inventory) ((CraftInventory) view.getBottomInventory()).getInventory();
            ((IMixinScreenHandler)this).getTrackedStacksBF().clear();
            this.slots.clear();
            if (typeChanged) setupSlots(top, bottom, player.getHandle());
            int size = getSize();
            player.getHandle().connection.send(new ClientboundOpenScreenPacket(this.containerId, type, net.minecraft.network.chat.Component.nullToEmpty(cachedTitle)));
            player.updateInventory();
        }
        return true;
    }

    public static net.minecraft.world.inventory.MenuType getNotchInventoryType(Inventory inventory) {
        switch (inventory.getType()) {
            case PLAYER:
            case CHEST:
            case ENDER_CHEST:
            case BARREL:
                switch (inventory.getSize()) {
                    case 9:
                        return net.minecraft.world.inventory.MenuType.GENERIC_9x1;
                    case 18:
                        return net.minecraft.world.inventory.MenuType.GENERIC_9x2;
                    case 27:
                        return net.minecraft.world.inventory.MenuType.GENERIC_9x3;
                    case 36:
                    case 41: // PLAYER
                        return net.minecraft.world.inventory.MenuType.GENERIC_9x4;
                    case 45:
                        return net.minecraft.world.inventory.MenuType.GENERIC_9x5;
                    case 54:
                        return net.minecraft.world.inventory.MenuType.GENERIC_9x6;
                    default:
                        throw new IllegalArgumentException("Unsupported custom inventory size " + inventory.getSize());
                }
            case WORKBENCH:
                return net.minecraft.world.inventory.MenuType.CRAFTING;
            case FURNACE:
                return net.minecraft.world.inventory.MenuType.FURNACE;
            case DISPENSER:
                return net.minecraft.world.inventory.MenuType.GENERIC_3x3;
            case ENCHANTING:
                return net.minecraft.world.inventory.MenuType.ENCHANTMENT;
            case BREWING:
                return net.minecraft.world.inventory.MenuType.BREWING_STAND;
            case BEACON:
                return net.minecraft.world.inventory.MenuType.BEACON;
            case ANVIL:
                return net.minecraft.world.inventory.MenuType.ANVIL;
            case SMITHING:
                return net.minecraft.world.inventory.MenuType.SMITHING;
            case HOPPER:
                return net.minecraft.world.inventory.MenuType.HOPPER;
            case DROPPER:
                return net.minecraft.world.inventory.MenuType.GENERIC_3x3;
            case SHULKER_BOX:
                return net.minecraft.world.inventory.MenuType.SHULKER_BOX;
            case BLAST_FURNACE:
                return net.minecraft.world.inventory.MenuType.BLAST_FURNACE;
            case LECTERN:
                return net.minecraft.world.inventory.MenuType.LECTERN;
            case SMOKER:
                return net.minecraft.world.inventory.MenuType.SMOKER;
            case LOOM:
                return net.minecraft.world.inventory.MenuType.LOOM;
            case CARTOGRAPHY:
                return net.minecraft.world.inventory.MenuType.CARTOGRAPHY_TABLE;
            case GRINDSTONE:
                return net.minecraft.world.inventory.MenuType.GRINDSTONE;
            case STONECUTTER:
                return net.minecraft.world.inventory.MenuType.STONECUTTER;
            case CREATIVE:
            case CRAFTING:
            case MERCHANT:
                throw new IllegalArgumentException("Can't open a " + inventory.getType() + " inventory!");
            default:
                return net.minecraft.world.inventory.MenuType.GENERIC_9x3;
        }
    }

    private void setupSlots(net.minecraft.world.Container top, net.minecraft.world.entity.player.Inventory bottom, Player entityhuman) {
        int windowId = -1;
        switch (cachedType) {
            case CREATIVE:
                break;
            case PLAYER:
            case CHEST:
            case ENDER_CHEST:
            case BARREL:
                delegate = new ChestMenu(net.minecraft.world.inventory.MenuType.GENERIC_9x3, windowId, bottom, top, top.getContainerSize() / 9);
                break;
            case DISPENSER:
            case DROPPER:
                delegate = new DispenserMenu(windowId, bottom, top);
                break;
            case FURNACE:
                delegate = new FurnaceMenu(windowId, bottom, top, new SimpleContainerData(4));
                break;
            case CRAFTING:
            case WORKBENCH:
                setupWorkbench(top, bottom);
                break;
            case ENCHANTING:
                delegate = new EnchantmentMenu(windowId, bottom);
                break;
            case BREWING:
                delegate = new BrewingStandMenu(windowId, bottom, top, new SimpleContainerData(2));
                break;
            case HOPPER:
                delegate = new HopperMenu(windowId, bottom, top);
                break;
            case ANVIL:
                delegate = new AnvilMenu(windowId, bottom);
                break;
            case SMITHING:
                delegate = new SmithingMenu(windowId, bottom);
                break;
            case BEACON:
                delegate = new BeaconMenu(windowId, bottom);
                break;
            case SHULKER_BOX:
                delegate = new ShulkerBoxMenu(windowId, bottom, top);
                break;
            case BLAST_FURNACE:
                delegate = new BlastFurnaceMenu(windowId, bottom, top, new SimpleContainerData(4));
                break;
            case LECTERN:
                delegate = new LecternMenu(windowId, top, new SimpleContainerData(1)/*, bottom*/); // need to port ContainerLoom.patch
                break;
            case SMOKER:
                delegate = new SmokerMenu(windowId, bottom, top, new SimpleContainerData(4));
                break;
            case LOOM:
                delegate = new LoomMenu(windowId, bottom);
                break;
            case CARTOGRAPHY:
                delegate = new CartographyTableMenu(windowId, bottom);
                break;
            case GRINDSTONE:
                delegate = new GrindstoneMenu(windowId, bottom);
                break;
            case STONECUTTER:
                delegate = new StonecutterMenu(windowId, bottom);
                break;
            case MERCHANT:
                delegate = new MerchantMenu(windowId, bottom);
                break;
        }

        if (delegate != null) {
            ((IMixinScreenHandler)this).setTrackedStacksBF(((IMixinScreenHandler)delegate).getTrackedStacksBF());
            ((IMixinScreenHandler)this).cardboard_setSlots(delegate.slots);
            ((IMixinScreenHandler)this).cardboard_previousTrackedStacks(((IMixinScreenHandler)delegate).cardboard_previousTrackedStacks());
        }

        if (cachedType == InventoryType.WORKBENCH) delegate = new CraftingMenu(windowId, bottom); // SPIGOT-4598 bug
    }

    private void setupWorkbench(net.minecraft.world.Container top, net.minecraft.world.Container bottom) {
        this.addSlot(new Slot(top, 0, 124, 35));

        int row;
        int col;

        for (row = 0; row < 3; ++row)
            for (col = 0; col < 3; ++col) this.addSlot(new Slot(top, 1 + col + row * 3, 30 + col * 18, 17 + row * 18));

        for (row = 0; row < 3; ++row)
            for (col = 0; col < 9; ++col)
                this.addSlot(new Slot(bottom, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));

        for (col=0; col<9; ++col) this.addSlot(new Slot(bottom, col, 8 + col * 18, 142));
    }

    @Override
    public ItemStack quickMoveStack(Player entityhuman, int i) {
    	if (delegate == null) {
    		return null;
    	}
        return delegate.quickMoveStack(entityhuman, i);
    	//return (delegate != null) ? delegate.transferSlot(entityhuman, i) : super.transferSlot(entityhuman, i);
    }

    @Override
    public boolean stillValid(Player entity) {
        return true;
    }

    @Override
    public net.minecraft.world.inventory.MenuType<?> getType() {
        return getNotchInventoryType(view.getTopInventory());
    }

}

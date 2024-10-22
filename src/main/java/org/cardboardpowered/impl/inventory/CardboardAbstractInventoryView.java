package org.cardboardpowered.impl.inventory;

import com.google.common.base.Preconditions;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CardboardAbstractInventoryView implements InventoryView  {

	@Override
    public void setItem(int slot, @Nullable ItemStack item) {
        Inventory inventory = this.getInventory(slot);
        if (inventory != null) {
            inventory.setItem(this.convertSlot(slot), item);
        } else if (item != null) {
            this.getPlayer().getWorld().dropItemNaturally(this.getPlayer().getLocation(), item);
        }
    }

    @Override
    public ItemStack getItem(int slot) {
        Inventory inventory = this.getInventory(slot);
        return inventory == null ? null : inventory.getItem(this.convertSlot(slot));
    }

    @Override
    public void setCursor(@Nullable ItemStack item) {
        this.getPlayer().setItemOnCursor(item);
    }

    @Override
    public ItemStack getCursor() {
        return this.getPlayer().getItemOnCursor();
    }
	
	@Override
    public Inventory getInventory(int rawSlot) {
        if (rawSlot == -999 || rawSlot == -1) {
            return null;
        }
        Preconditions.checkArgument((rawSlot >= 0 ? 1 : 0) != 0, "Negative, non outside slot %s", rawSlot);
        Preconditions.checkArgument((rawSlot < this.countSlots() ? 1 : 0) != 0, "Slot %s greater than inventory slot count", rawSlot);
        if (rawSlot < this.getTopInventory().getSize()) {
            return this.getTopInventory();
        }
        return this.getBottomInventory();
    }
	
	@Override
    public int convertSlot(int rawSlot) {
        int numInTop = this.getTopInventory().getSize();
        if (rawSlot < numInTop) {
            return rawSlot;
        }
        int slot = rawSlot - numInTop;
        if (this.getType() == InventoryType.CRAFTING || this.getType() == InventoryType.CREATIVE) {
            if (slot < 4) {
                return 39 - slot;
            }
            if (slot > 39) {
                return slot;
            }
            slot -= 4;
        }
        slot = slot >= 27 ? (slot -= 27) : (slot += 9);
        return slot;
    }
	
	@Override
    public InventoryType.SlotType getSlotType(int slot) {
        InventoryType.SlotType type = InventoryType.SlotType.CONTAINER;
        if (slot >= 0 && slot < this.getTopInventory().getSize()) {
            switch (this.getType()) {
                case BLAST_FURNACE: 
                case FURNACE: 
                case SMOKER: {
                    if (slot == 2) {
                        type = InventoryType.SlotType.RESULT;
                        break;
                    }
                    if (slot == 1) {
                        type = InventoryType.SlotType.FUEL;
                        break;
                    }
                    type = InventoryType.SlotType.CRAFTING;
                    break;
                }
                case BREWING: {
                    if (slot == 3) {
                        type = InventoryType.SlotType.FUEL;
                        break;
                    }
                    type = InventoryType.SlotType.CRAFTING;
                    break;
                }
                case ENCHANTING: {
                    type = InventoryType.SlotType.CRAFTING;
                    break;
                }
                case WORKBENCH: 
                case CRAFTING: {
                    if (slot == 0) {
                        type = InventoryType.SlotType.RESULT;
                        break;
                    }
                    type = InventoryType.SlotType.CRAFTING;
                    break;
                }
                case BEACON: {
                    type = InventoryType.SlotType.CRAFTING;
                    break;
                }
                case ANVIL: 
                case CARTOGRAPHY: 
                case GRINDSTONE: 
                case MERCHANT: {
                    if (slot == 2) {
                        type = InventoryType.SlotType.RESULT;
                        break;
                    }
                    type = InventoryType.SlotType.CRAFTING;
                    break;
                }
                case STONECUTTER: {
                    if (slot == 1) {
                        type = InventoryType.SlotType.RESULT;
                        break;
                    }
                    type = InventoryType.SlotType.CRAFTING;
                    break;
                }
                case LOOM: 
                case SMITHING: 
                case SMITHING_NEW: {
                    if (slot == 3) {
                        type = InventoryType.SlotType.RESULT;
                        break;
                    }
                    type = InventoryType.SlotType.CRAFTING;
                    break;
                }
            }
        } else if (slot < 0) {
            type = InventoryType.SlotType.OUTSIDE;
        } else if (this.getType() == InventoryType.CRAFTING) {
            if (slot < 9) {
                type = InventoryType.SlotType.ARMOR;
            } else if (slot > 35) {
                type = InventoryType.SlotType.QUICKBAR;
            }
        } else if (slot >= this.countSlots() - 14) {
            type = InventoryType.SlotType.QUICKBAR;
        }
        return type;
    }

	@Override
    public void close() {
        this.getPlayer().closeInventory();
    }

	@Override
    public int countSlots() {
        return this.getTopInventory().getSize() + this.getBottomInventory().getSize();
    }

	@Override
    public boolean setProperty(@NotNull InventoryView.Property prop, int value) {
        return this.getPlayer().setWindowProperty(prop, value);
    }

}

package org.cardboardpowered.mixin.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.cardboardpowered.impl.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import org.cardboardpowered.interfaces.IMixinInventory;
import org.cardboardpowered.interfaces.IMixinPlayerInventory;
import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;

@Mixin(Inventory.class)
public class MixinPlayerInventory implements IMixinInventory, IMixinPlayerInventory {

	@Unique
    private Inventory get() {
        return (Inventory) (Object) this;
    }

    @Override
    public List<ItemStack> getContents() {
        // TODO Auto-generated method stub
        return get().items;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        get().startOpen((Player) who.nms);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        get().stopOpen((Player) who.nms);
    }

    @Override
    public List<HumanEntity> getViewers() {
        // TODO Auto-generated method stub
        return Arrays.asList(((CraftPlayer)((IMixinServerEntityPlayer)get().player).getBukkitEntity()));
    }

    @Override
    public InventoryHolder getOwner() {
        return ((CraftPlayer)((IMixinServerEntityPlayer)get().player).getBukkitEntity());
    }

    @Override
    public void setCardboardMaxStackSize(int size) {
    }

    @Override
    public Location getLocation() {
        return ((CraftPlayer)((IMixinServerEntityPlayer)get().player).getBukkitEntity()).getLocation();
    }

    @Override
    public int getCardboardMaxStackSize() {
        return get().getMaxStackSize();
    }

    @Override
    public int canHold(ItemStack itemstack) {
        int remains = itemstack.getCount();
        for (int i = 0; i < get().items.size(); ++i) {
            ItemStack itemstack1 = get().getItem(i);
            if (itemstack1.isEmpty()) return itemstack.getCount();

            if (get().hasRemainingSpaceForItem(itemstack1, itemstack))
                remains -= (itemstack1.getMaxStackSize() < getCardboardMaxStackSize() ? itemstack1.getMaxStackSize() : getCardboardMaxStackSize()) - itemstack1.getCount();
            if (remains <= 0) return itemstack.getCount();
        }

        ItemStack offhandItemStack = get().equipment.get(EquipmentSlot.OFFHAND); // get().getStack(get().main.size() + get().armor.size());
        if (get().hasRemainingSpaceForItem(offhandItemStack, itemstack))
            remains -= (offhandItemStack.getMaxStackSize() < get().getMaxStackSize() ? offhandItemStack.getMaxStackSize() : get().getMaxStackSize()) - offhandItemStack.getCount();
        if (remains <= 0) return itemstack.getCount();

        return itemstack.getCount() - remains;
    }
    
    private static final EquipmentSlot[] EQUIPMENT_SLOTS_SORTED_BY_INDEX = (EquipmentSlot[])Inventory.EQUIPMENT_SLOT_MAPPING.int2ObjectEntrySet().stream().sorted(Comparator.comparingInt(Int2ObjectMap.Entry::getIntKey)).map(Map.Entry::getValue).toArray(EquipmentSlot[]::new);
    
    @Override
    public List<ItemStack> getArmorContents() {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>(4);
        for (EquipmentSlot equipmentSlot : EQUIPMENT_SLOTS_SORTED_BY_INDEX) {
            if (equipmentSlot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
            items.add(get().equipment.get(equipmentSlot));
        }
        return items;
    }

    @Override
    public List<ItemStack> getExtraContent() {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (EquipmentSlot equipmentSlot : EQUIPMENT_SLOTS_SORTED_BY_INDEX) {
            if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) continue;
            items.add(get().equipment.get(equipmentSlot));
        }
        return items;
    }

}
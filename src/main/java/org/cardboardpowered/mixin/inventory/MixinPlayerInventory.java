package org.cardboardpowered.mixin.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

@Mixin(PlayerInventory.class)
public class MixinPlayerInventory implements IMixinInventory, IMixinPlayerInventory {

	@Unique
    private PlayerInventory get() {
        return (PlayerInventory) (Object) this;
    }

    @Override
    public List<ItemStack> getContents() {
        // TODO Auto-generated method stub
        return get().main;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        get().onOpen((PlayerEntity) who.nms);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        get().onClose((PlayerEntity) who.nms);
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
    public void setMaxStackSize(int size) {
    }

    @Override
    public Location getLocation() {
        return ((CraftPlayer)((IMixinServerEntityPlayer)get().player).getBukkitEntity()).getLocation();
    }

    @Override
    public int getMaxStackSize() {
        return get().getMaxCountPerStack();
    }

    @Override
    public int canHold(ItemStack itemstack) {
        int remains = itemstack.getCount();
        for (int i = 0; i < get().main.size(); ++i) {
            ItemStack itemstack1 = get().getStack(i);
            if (itemstack1.isEmpty()) return itemstack.getCount();

            if (get().canStackAddMore(itemstack1, itemstack))
                remains -= (itemstack1.getMaxCount() < getMaxStackSize() ? itemstack1.getMaxCount() : getMaxStackSize()) - itemstack1.getCount();
            if (remains <= 0) return itemstack.getCount();
        }

        ItemStack offhandItemStack = get().equipment.get(EquipmentSlot.OFFHAND); // get().getStack(get().main.size() + get().armor.size());
        if (get().canStackAddMore(offhandItemStack, itemstack))
            remains -= (offhandItemStack.getMaxCount() < get().getMaxCountPerStack() ? offhandItemStack.getMaxCount() : get().getMaxCountPerStack()) - offhandItemStack.getCount();
        if (remains <= 0) return itemstack.getCount();

        return itemstack.getCount() - remains;
    }
    
    private static final EquipmentSlot[] EQUIPMENT_SLOTS_SORTED_BY_INDEX = (EquipmentSlot[])PlayerInventory.EQUIPMENT_SLOTS.int2ObjectEntrySet().stream().sorted(Comparator.comparingInt(Int2ObjectMap.Entry::getIntKey)).map(Map.Entry::getValue).toArray(EquipmentSlot[]::new);
    
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
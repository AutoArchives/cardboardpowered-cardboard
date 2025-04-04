package org.cardboardpowered.mixin.inventory;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import org.cardboardpowered.impl.entity.CraftAbstractVillager;
import org.cardboardpowered.interfaces.IMixinEntity;
import org.cardboardpowered.interfaces.IMixinInventory;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.Merchant;
import net.minecraft.village.MerchantInventory;

@Mixin(MerchantInventory.class)
public class MixinTraderInventory implements IMixinInventory {

    @Shadow
    public DefaultedList<ItemStack> inventory;

    @Shadow
    public Merchant merchant;

    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;

    @Override
    public List<ItemStack> getContents() {
        return this.inventory;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
        merchant.setCustomer((PlayerEntity) null);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return transaction;
    }

    @Override
    public int getMaxStackSize() {
        return maxStack;
    }

    @Override
    public void setMaxStackSize(int i) {
        maxStack = i;
    }

    @Override
    public org.bukkit.inventory.InventoryHolder getOwner() {
        return (merchant instanceof MerchantEntity) ? (CraftAbstractVillager) ((IMixinEntity)((MerchantEntity) this.merchant)).getBukkitEntity() : null;
    }

    @Override
    public Location getLocation() {
        return (merchant instanceof VillagerEntity) ? ((IMixinEntity)((VillagerEntity) this.merchant)).getBukkitEntity().getLocation() : null;
    }

}
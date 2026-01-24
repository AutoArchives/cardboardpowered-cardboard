package org.cardboardpowered.mixin.inventory;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import org.cardboardpowered.impl.entity.CraftAbstractVillager;
import org.cardboardpowered.interfaces.IMixinEntity;
import org.cardboardpowered.interfaces.IMixinInventory;

@Mixin(MerchantContainer.class)
public class MixinTraderInventory implements IMixinInventory {

    @Shadow
    public NonNullList<ItemStack> itemStacks;

    @Shadow
    public Merchant merchant;

    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;

    @Override
    public List<ItemStack> getContents() {
        return this.itemStacks;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
        merchant.setTradingPlayer((Player) null);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return transaction;
    }

    @Override
    public int getCardboardMaxStackSize() {
        return maxStack;
    }

    @Override
    public void setCardboardMaxStackSize(int i) {
        maxStack = i;
    }

    @Override
    public org.bukkit.inventory.InventoryHolder getOwner() {
        return (merchant instanceof AbstractVillager) ? (CraftAbstractVillager) ((IMixinEntity)((AbstractVillager) this.merchant)).getBukkitEntity() : null;
    }

    @Override
    public Location getLocation() {
        return (merchant instanceof Villager) ? ((IMixinEntity)((Villager) this.merchant)).getBukkitEntity().getLocation() : null;
    }

}
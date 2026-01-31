package org.cardboardpowered.mixin.screen;

import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import org.bukkit.entity.Player;
import org.cardboardpowered.impl.inventory.CardboardAnvilInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import org.cardboardpowered.bridge.world.inventory.AnvilMenuBridge;
import org.cardboardpowered.bridge.world.inventory.ContainerLevelAccessBridge;
import org.cardboardpowered.bridge.server.level.ServerPlayerBridge;

@Mixin(AnvilMenu.class)
public class MixinAnvilScreenHandler extends MixinForgingScreenHandler implements AnvilMenuBridge {

    // TODO Add AnvilPrepareEvent

    public int maximumRepairCost_BF = 40;
    public CraftInventoryView bukkitEntity;

    @Shadow public String itemName;
    @Shadow public DataSlot cost;

    @Override
    public CraftInventoryView getBukkitView() {
        if (bukkitEntity != null)
            return bukkitEntity;

        org.bukkit.craftbukkit.inventory.CraftInventory inventory = new CardboardAnvilInventory(
                ((ContainerLevelAccessBridge)access).getLocation(), this.inputSlots, this.resultSlots, (AnvilMenu)(Object)this);
        bukkitEntity = new CraftInventoryView((Player)((ServerPlayerBridge)this.player).getBukkitEntity(), inventory, (AnvilMenu)(Object)this);
        return bukkitEntity;
    }

    @Override
    public String getNewItemName_BF() {
        return itemName;
    }

    @Override
    public int getLevelCost_BF() {
        return cost.get();
    }

    @Override
    public void setLevelCost_BF(int i) {
        cost.set(i);
    }

    @Override
    public int getMaxRepairCost_BF() {
        return maximumRepairCost_BF;
    }

    @Override
    public void setMaxRepairCost_BF(int levels) {
        maximumRepairCost_BF = levels;
    }

}
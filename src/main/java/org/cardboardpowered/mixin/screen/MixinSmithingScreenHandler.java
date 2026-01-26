package org.cardboardpowered.mixin.screen;

import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.cardboardpowered.impl.inventory.CardboardSmithingInventory;
import net.minecraft.world.inventory.SmithingMenu;
import org.bukkit.entity.Player;
import org.spongepowered.asm.mixin.Mixin;

import org.cardboardpowered.bridge.server.level.ServerPlayerBridge;

@Mixin(SmithingMenu.class)
public class MixinSmithingScreenHandler extends MixinForgingScreenHandler {

    private CardboardInventoryView bukkitEntity;

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity != null) return bukkitEntity;

        CardboardSmithingInventory inventory = new CardboardSmithingInventory(this.inputSlots, this.resultSlots);
        bukkitEntity = new CardboardInventoryView((Player)((ServerPlayerBridge)this.player).getBukkitEntity(), inventory, (SmithingMenu)(Object)this);
        return bukkitEntity;
    }

}
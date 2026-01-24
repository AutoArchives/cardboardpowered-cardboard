package org.cardboardpowered.mixin.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.entity.Player;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;

@Mixin(InventoryMenu.class)
public class MixinPlayerScreenHandler extends MixinScreenHandler implements MenuProvider {

	// @Shadow public RecipeInputInventory craftingInput;
    // @Shadow private CraftingResultInventory craftingResult;
    private CardboardInventoryView bukkitEntity = null;
    private Inventory player;

    @Inject(method = "<init>", at = @At("TAIL"))
    
    
    public void setPlayerInv(Inventory playerinventory, boolean flag, net.minecraft.world.entity.player.Player entityhuman, CallbackInfo ci) {
       // this.craftingResult = new CraftingResultInventory();
       // this.craftingInput = new CraftingInventory((PlayerScreenHandler)(Object)this, 2, 2);
        this.player = playerinventory;
        
        // TODO: 1.19:
        // setTitle(new TranslatableText("container.crafting"));
    }

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity != null)
            return bukkitEntity;
        
        InventoryMenu thiz = (InventoryMenu)(Object)this;

        CraftInventoryCrafting inventory = new CraftInventoryCrafting(thiz.craftSlots, thiz.resultSlots);
        bukkitEntity = new CardboardInventoryView((Player)((IMixinServerEntityPlayer)this.player.player).getBukkitEntity(), inventory, (InventoryMenu)(Object)this);
        return bukkitEntity;
    }

    @Override
    public AbstractContainerMenu createMenu(int arg0, Inventory arg1, net.minecraft.world.entity.player.Player arg2) {
        return new InventoryMenu(arg1, true, arg2);
    }

    @Override
    public Component getDisplayName() {
        return this.getTitle();
    }

}
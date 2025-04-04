package org.cardboardpowered.mixin.screen;

import org.cardboardpowered.interfaces.IMixinForgingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;

/**
 * Yarn:	ForgingScreenHandler
 * Mojmap:	ItemCombinerMenu
 * 
 * @implNote We are currently missing impl
 * 
 * @see {@link org.cardboardpowered.interfaces.IMixinForgingScreenHandler}
 * @implSpec https://github.com/PaperMC/Paper/blob/main/paper-server/patches/sources/net/minecraft/world/inventory/ItemCombinerMenu.java.patch
 */
@Mixin(ForgingScreenHandler.class)
public abstract class MixinForgingScreenHandler extends MixinScreenHandler implements IMixinForgingScreenHandler {

    @Shadow
    public CraftingResultInventory output = new CraftingResultInventory();

    @Shadow
    public Inventory input;

    @Shadow
    public ScreenHandlerContext context;

    @Shadow
    public PlayerEntity player;


}
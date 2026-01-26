package org.cardboardpowered.mixin.screen;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ResultContainer;
import org.cardboardpowered.interfaces.IMixinForgingScreenHandler;
import org.cardboardpowered.mixin.world.inventory.AbstractContainerMenuMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Yarn:	ForgingScreenHandler
 * Mojmap:	ItemCombinerMenu
 * 
 * @implNote We are currently missing impl
 * 
 * @see {@link org.cardboardpowered.interfaces.IMixinForgingScreenHandler}
 * @implSpec https://github.com/PaperMC/Paper/blob/main/paper-server/patches/sources/net/minecraft/world/inventory/ItemCombinerMenu.java.patch
 */
@Mixin(ItemCombinerMenu.class)
public abstract class MixinForgingScreenHandler extends AbstractContainerMenuMixin implements IMixinForgingScreenHandler {

    @Shadow
    public ResultContainer resultSlots = new ResultContainer();

    @Shadow
    public Container inputSlots;

    @Shadow
    public ContainerLevelAccess access;

    @Shadow
    public Player player;


}
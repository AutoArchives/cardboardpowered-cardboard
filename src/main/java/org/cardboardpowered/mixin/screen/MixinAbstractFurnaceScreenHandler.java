package org.cardboardpowered.mixin.screen;

import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.bukkit.entity.Player;
import org.cardboardpowered.impl.inventory.CardboardFurnaceInventory;
import org.cardboardpowered.mixin.world.inventory.AbstractContainerMenuMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cardboardpowered.interfaces.IMixinEntity;

@Mixin(AbstractFurnaceMenu.class)
public class MixinAbstractFurnaceScreenHandler extends AbstractContainerMenuMixin {

    @Shadow
    public Container container;

    private CardboardInventoryView bukkitEntity = null;
    private Inventory playerInv;

    // Caused by: org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException: Invalid descriptor on bukkitfabric.mixins.json:screen.MixinAbstractFurnaceScreenHandler from mod cardboard->@Inject:
    // setPlayerInv(Lnet/minecraft/screen/ScreenHandlerType;Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/recipe/book/RecipeBookCategory;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V!
    // Expected    (Lnet/minecraft/screen/ScreenHandlerType;Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/recipe/book/RecipeBookType;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V
    // but found   (Lnet/minecraft/screen/ScreenHandlerType;Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/recipe/book/RecipeBookCategory;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V [INJECT_APPLY Applicator Phase -> bukkitfabric.mixins.json:screen.MixinAbstractFurnaceScreenHandler from mod cardboard -> Apply Injections ->  -> Inject -> bukkitfabric.mixins.json:screen.MixinAbstractFurnaceScreenHandler from mod cardboard->@Inject::setPlayerInv(Lnet/minecraft/screen/ScreenHandlerType;Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/recipe/book/RecipeBookCategory;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V]

   /*
    * "Lnet/minecraft/screen/AbstractFurnaceScreenHandler;<init>(
    * Lnet/minecraft/screen/ScreenHandlerType;
    * Lnet/minecraft/recipe/RecipeType;
    * Lnet/minecraft/registry/RegistryKey;
    * Lnet/minecraft/recipe/book/RecipeBookType;
    * I
    * Lnet/minecraft/entity/player/PlayerInventory;
    * Lnet/minecraft/inventory/Inventory;
    * Lnet/minecraft/screen/PropertyDelegate;
    * )V",
    */

    
    @Inject(
    	method =
    		   "<init>(Lnet/minecraft/world/inventory/MenuType;Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/inventory/RecipeBookType;ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V",
    		// "Lnet/minecraft/screen/AbstractFurnaceScreenHandler;<init>(Lnet/minecraft/screen/ScreenHandlerType;Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/recipe/book/RecipeBookType;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;)V",
    		// "<init>(Lnet/minecraft/screen/ScreenHandlerType;Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/recipe/book/RecipeBookCategory;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;)V",
    	at = @At("TAIL")
    )
    public void setPlayerInv(
    		MenuType<?> sh,
    		RecipeType<? extends AbstractCookingRecipe> recipes,
    		ResourceKey key,
    		RecipeBookType type,
    		int i,
    		Inventory playerinventory,
    		Container inv,
    		ContainerData prop,
    		CallbackInfo ci
    	) {
        this.playerInv = playerinventory;
    }

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity != null) return bukkitEntity;

        CardboardFurnaceInventory inventory = new CardboardFurnaceInventory((AbstractFurnaceBlockEntity) this.container);
        bukkitEntity = new CardboardInventoryView((Player)((IMixinEntity)this.playerInv.player).getBukkitEntity(), inventory, (AbstractFurnaceMenu)(Object)this);
        return bukkitEntity;
    }


}
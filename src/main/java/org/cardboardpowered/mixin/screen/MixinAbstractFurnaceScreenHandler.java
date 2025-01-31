package org.cardboardpowered.mixin.screen;

import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.bukkit.entity.Player;
import org.cardboardpowered.impl.inventory.CardboardFurnaceInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.javazilla.bukkitfabric.interfaces.IMixinEntity;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;

@Mixin(AbstractFurnaceScreenHandler.class)
public class MixinAbstractFurnaceScreenHandler extends MixinScreenHandler {

    @Shadow
    public Inventory inventory;

    private CardboardInventoryView bukkitEntity = null;
    private PlayerInventory playerInv;

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
    		   "Lnet/minecraft/screen/AbstractFurnaceScreenHandler;<init>(Lnet/minecraft/screen/ScreenHandlerType;Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/recipe/book/RecipeBookType;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;)V",
    		// "Lnet/minecraft/screen/AbstractFurnaceScreenHandler;<init>(Lnet/minecraft/screen/ScreenHandlerType;Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/recipe/book/RecipeBookType;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;)V",
    		// "<init>(Lnet/minecraft/screen/ScreenHandlerType;Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/recipe/book/RecipeBookCategory;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;)V",
    	at = @At("TAIL")
    )
    public void setPlayerInv(
    		ScreenHandlerType<?> sh,
    		RecipeType<? extends AbstractCookingRecipe> recipes,
    		RegistryKey key,
    		RecipeBookType type,
    		int i,
    		PlayerInventory playerinventory,
    		Inventory inv,
    		PropertyDelegate prop,
    		CallbackInfo ci
    	) {
        this.playerInv = playerinventory;
    }

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity != null) return bukkitEntity;

        CardboardFurnaceInventory inventory = new CardboardFurnaceInventory((AbstractFurnaceBlockEntity) this.inventory);
        bukkitEntity = new CardboardInventoryView((Player)((IMixinEntity)this.playerInv.player).getBukkitEntity(), inventory, (AbstractFurnaceScreenHandler)(Object)this);
        return bukkitEntity;
    }


}
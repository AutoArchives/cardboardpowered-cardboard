package org.bukkit.craftbukkit.inventory.util;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.text.Text;
import org.bukkit.craftbukkit.inventory.CraftMenuType;
import org.bukkit.craftbukkit.inventory.util.CraftMenuBuilder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.inventory.view.BeaconView;
import org.bukkit.inventory.view.BrewingStandView;
import org.bukkit.inventory.view.CrafterView;
import org.bukkit.inventory.view.EnchantmentView;
import org.bukkit.inventory.view.FurnaceView;
import org.bukkit.inventory.view.LecternView;
import org.bukkit.inventory.view.LoomView;
import org.bukkit.inventory.view.MerchantView;
import org.bukkit.inventory.view.StonecutterView;

import com.github.bsideup.jabel.Desugar;
import com.javazilla.bukkitfabric.interfaces.IMixinServerEntityPlayer;


public final class CraftMenus {
    private static final CraftMenuBuilder STANDARD = (player, menuType) -> menuType.create(((IMixinServerEntityPlayer)player).nextContainerCounter(), player.getInventory());

    public static <V extends InventoryView> MenuTypeData<V> getMenuTypeData(CraftMenuType<?> menuType) {
    	if (menuType == MenuType.GENERIC_3X3) {
            return CraftMenus.asType(new MenuTypeData<InventoryView>(InventoryView.class, CraftMenuBuilder.tileEntity(DispenserBlockEntity::new, Blocks.DISPENSER)));
        }
        if (menuType == MenuType.CRAFTER_3X3) {
            return CraftMenus.asType(new MenuTypeData<CrafterView>(CrafterView.class, CraftMenuBuilder.tileEntity(CrafterBlockEntity::new, Blocks.CRAFTER)));
        }
        if (menuType == MenuType.ANVIL) {
            return CraftMenus.asType(new MenuTypeData<AnvilView>(AnvilView.class, CraftMenuBuilder.worldAccess(AnvilScreenHandler::new)));
        }
        if (menuType == MenuType.BEACON) {
            return CraftMenus.asType(new MenuTypeData<BeaconView>(BeaconView.class, CraftMenuBuilder.tileEntity(BeaconBlockEntity::new, Blocks.BEACON)));
        }
        if (menuType == MenuType.BLAST_FURNACE) {
            return CraftMenus.asType(new MenuTypeData<FurnaceView>(FurnaceView.class, CraftMenuBuilder.tileEntity(BlastFurnaceBlockEntity::new, Blocks.BLAST_FURNACE)));
        }
        if (menuType == MenuType.BREWING_STAND) {
            return CraftMenus.asType(new MenuTypeData<BrewingStandView>(BrewingStandView.class, CraftMenuBuilder.tileEntity(BrewingStandBlockEntity::new, Blocks.BREWING_STAND)));
        }
        if (menuType == MenuType.CRAFTING) {
            return CraftMenus.asType(new MenuTypeData<InventoryView>(InventoryView.class, CraftMenuBuilder.worldAccess(CraftingScreenHandler::new)));
        }
        if (menuType == MenuType.ENCHANTMENT) {
            return CraftMenus.asType(new MenuTypeData<EnchantmentView>(EnchantmentView.class, (player, type) -> new SimpleNamedScreenHandlerFactory((syncId, inventory, human) -> CraftMenuBuilder.worldAccess(EnchantmentScreenHandler::new).build(player, type), Text.empty()).createMenu(((IMixinServerEntityPlayer)player).nextContainerCounter(), player.getInventory(), player)));
        }
        if (menuType == MenuType.FURNACE) {
            return CraftMenus.asType(new MenuTypeData<FurnaceView>(FurnaceView.class, CraftMenuBuilder.tileEntity(FurnaceBlockEntity::new, Blocks.FURNACE)));
        }
        if (menuType == MenuType.GRINDSTONE) {
            return CraftMenus.asType(new MenuTypeData<InventoryView>(InventoryView.class, CraftMenuBuilder.worldAccess(GrindstoneScreenHandler::new)));
        }
        if (menuType == MenuType.HOPPER) {
            return CraftMenus.asType(new MenuTypeData<InventoryView>(InventoryView.class, CraftMenuBuilder.tileEntity(HopperBlockEntity::new, Blocks.HOPPER)));
        }
        if (menuType == MenuType.LECTERN) {
            return CraftMenus.asType(new MenuTypeData<LecternView>(LecternView.class, CraftMenuBuilder.tileEntity(LecternBlockEntity::new, Blocks.LECTERN)));
        }
        if (menuType == MenuType.LOOM) {
            return CraftMenus.asType(new MenuTypeData<LoomView>(LoomView.class, STANDARD));
        }
        if (menuType == MenuType.MERCHANT) {
            return CraftMenus.asType(new MenuTypeData<MerchantView>(MerchantView.class, STANDARD));
        }
        if (menuType == MenuType.SMITHING) {
            return CraftMenus.asType(new MenuTypeData<InventoryView>(InventoryView.class, CraftMenuBuilder.worldAccess(SmithingScreenHandler::new)));
        }
        if (menuType == MenuType.SMOKER) {
            return CraftMenus.asType(new MenuTypeData<FurnaceView>(FurnaceView.class, CraftMenuBuilder.tileEntity(SmokerBlockEntity::new, Blocks.SMOKER)));
        }
        if (menuType == MenuType.CARTOGRAPHY_TABLE) {
            return CraftMenus.asType(new MenuTypeData<InventoryView>(InventoryView.class, CraftMenuBuilder.worldAccess(CartographyTableScreenHandler::new)));
        }
        if (menuType == MenuType.STONECUTTER) {
            return CraftMenus.asType(new MenuTypeData<StonecutterView>(StonecutterView.class, CraftMenuBuilder.worldAccess(StonecutterScreenHandler::new)));
        }
        return CraftMenus.asType(new MenuTypeData<InventoryView>(InventoryView.class, STANDARD));
    }

    private static <V extends InventoryView> MenuTypeData<V> asType(MenuTypeData<?> data) {
        return (MenuTypeData<V>) data;
    }

    @Desugar
    public record MenuTypeData<V extends InventoryView>(Class<V> viewClass, CraftMenuBuilder menuBuilder) {
    }
}


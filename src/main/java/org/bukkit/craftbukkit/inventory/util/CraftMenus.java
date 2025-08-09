package org.bukkit.craftbukkit.inventory.util;

import org.bukkit.craftbukkit.inventory.CraftMenuType;
import org.bukkit.craftbukkit.inventory.CraftMerchant;
import org.bukkit.craftbukkit.inventory.view.CraftMerchantView;
import org.bukkit.craftbukkit.inventory.view.builder.CraftAccessLocationInventoryViewBuilder;
import org.bukkit.craftbukkit.inventory.view.builder.CraftBlockEntityInventoryViewBuilder;
import org.bukkit.craftbukkit.inventory.view.builder.CraftDoubleChestInventoryViewBuilder;
import org.bukkit.craftbukkit.inventory.view.builder.CraftMerchantInventoryViewBuilder;
import org.bukkit.craftbukkit.inventory.view.builder.CraftStandardInventoryViewBuilder;
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
import org.bukkit.inventory.view.builder.InventoryViewBuilder;
import org.jspecify.annotations.NullMarked;

import org.cardboardpowered.interfaces.IMixinScreenHandler;
import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;

import java.util.function.Supplier;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOfferList;

@NullMarked
public final class CraftMenus {

    public record MenuTypeData<V extends InventoryView, B extends InventoryViewBuilder<V>>(Class<V> viewClass, Supplier<B> viewBuilder) {
    }

    // This is a temporary measure that will likely be removed with the rewrite of HumanEntity#open[] methods
    public static void openMerchantMenu(final ServerPlayerEntity player, final MerchantScreenHandler merchant) {
    	
    	IMixinScreenHandler ish = (IMixinScreenHandler) merchant;
    	CraftMerchantView view = (CraftMerchantView)  ish.getBukkitView();
    	CraftMerchant merch = (CraftMerchant) view.getMerchant();
    	final Merchant minecraftMerchant = merch.getMerchant();
    	
        // final Merchant minecraftMerchant = ((CraftMerchant) merchant.getBukkitView().getMerchant()).getMerchant();
        int level = 1;
        if (minecraftMerchant instanceof final VillagerEntity villager) {
            level = villager.getVillagerData().level();
        }

        if (minecraftMerchant.getCustomer() != null) { // merchant's can only have one trader
            ((IMixinServerEntityPlayer) minecraftMerchant.getCustomer()).closeHandledScreen();
        }

        minecraftMerchant.setCustomer(player);

        Text title = ish.getTitle();// Text.literal("TODO: CARDBOARD SET TITLE");
        // TODO: merchant.getTitle()

        player.networkHandler.sendPacket(new OpenScreenS2CPacket(merchant.syncId, net.minecraft.screen.ScreenHandlerType.MERCHANT, title));
        player.currentScreenHandler = merchant;
        player.onScreenHandlerOpened(merchant);
        // Copy IMerchant#openTradingScreen
        TradeOfferList merchantrecipelist = minecraftMerchant.getOffers();

        if (!merchantrecipelist.isEmpty()) {
            player.sendTradeOffers(merchant.syncId, merchantrecipelist, level, minecraftMerchant.getExperience(), minecraftMerchant.isLeveledMerchant(), minecraftMerchant.canRefreshTrades());
        }
        // End Copy IMerchant#openTradingScreen
    }

    public static <V extends InventoryView, B extends InventoryViewBuilder<V>> MenuTypeData<V, B> getMenuTypeData(final CraftMenuType<?, ?> menuType) {
        final net.minecraft.screen.ScreenHandlerType<?> handle = menuType.getHandle();
        // this sucks horribly but it should work for now
        if (menuType == MenuType.GENERIC_9X6) {
            return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftDoubleChestInventoryViewBuilder<>(handle)));
        }
        if (menuType == MenuType.GENERIC_9X3) {
            return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.CHEST, null)));
        }
        // this isn't ideal as both dispenser and dropper are 3x3, InventoryType can't currently handle generic 3x3s with size 9
        // this needs to be removed when inventory creation is overhauled
        if (menuType == MenuType.GENERIC_3X3) {
            return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.DISPENSER, DispenserBlockEntity::new)));
        }
        if (menuType == MenuType.CRAFTER_3X3) {
            return asType(new MenuTypeData<>(CrafterView.class, () -> new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.CRAFTER, CrafterBlockEntity::new)));
        }
        if (menuType == MenuType.ANVIL) {
            return asType(new MenuTypeData<>(AnvilView.class, () -> new CraftAccessLocationInventoryViewBuilder<>(handle, AnvilScreenHandler::new)));
        }
        if (menuType == MenuType.BEACON) {
            return asType(new MenuTypeData<>(BeaconView.class, () -> new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.BEACON, BeaconBlockEntity::new)));
        }
        if (menuType == MenuType.BLAST_FURNACE) {
            return asType(new MenuTypeData<>(FurnaceView.class, () -> new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.BLAST_FURNACE, BlastFurnaceBlockEntity::new)));
        }
        if (menuType == MenuType.BREWING_STAND) {
            return asType(new MenuTypeData<>(BrewingStandView.class, () -> new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.BREWING_STAND, BrewingStandBlockEntity::new)));
        }
        if (menuType == MenuType.CRAFTING) {
            return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftAccessLocationInventoryViewBuilder<>(handle, CraftingScreenHandler::new)));
        }
        if (menuType == MenuType.ENCHANTMENT) {
            return asType(new MenuTypeData<>(EnchantmentView.class, () -> new CraftAccessLocationInventoryViewBuilder<>(handle, EnchantmentScreenHandler::new)));
        }
        if (menuType == MenuType.FURNACE) {
            return asType(new MenuTypeData<>(FurnaceView.class, () -> new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.FURNACE, FurnaceBlockEntity::new)));
        }
        if (menuType == MenuType.GRINDSTONE) {
            return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftAccessLocationInventoryViewBuilder<>(handle, GrindstoneScreenHandler::new)));
        }
        // We really don't need to be creating a tile entity for hopper but currently InventoryType doesn't have capacity
        // to understand otherwise
        if (menuType == MenuType.HOPPER) {
            return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.HOPPER, HopperBlockEntity::new)));
        }
        // We also don't need to create a tile entity for lectern, but again InventoryType isn't smart enough to know any better
        if (menuType == MenuType.LECTERN) {
            return asType(new MenuTypeData<>(LecternView.class, () -> new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.LECTERN, LecternBlockEntity::new)));
        }
        if (menuType == MenuType.LOOM) {
            return asType(new MenuTypeData<>(LoomView.class, () -> new CraftStandardInventoryViewBuilder<>(handle)));
        }
        if (menuType == MenuType.MERCHANT) {
            return asType(new MenuTypeData<>(MerchantView.class, () -> new CraftMerchantInventoryViewBuilder<>(handle)));
        }
        if (menuType == MenuType.SHULKER_BOX) {
            return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.SHULKER_BOX, ShulkerBoxBlockEntity::new)));
        }
        if (menuType == MenuType.SMITHING) {
            return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftAccessLocationInventoryViewBuilder<>(handle, SmithingScreenHandler::new)));
        }
        if (menuType == MenuType.SMOKER) {
            return asType(new MenuTypeData<>(FurnaceView.class, () -> new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.SMOKER, SmokerBlockEntity::new)));
        }
        if (menuType == MenuType.CARTOGRAPHY_TABLE) {
            return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftAccessLocationInventoryViewBuilder<>(handle, CartographyTableScreenHandler::new)));
        }
        if (menuType == MenuType.STONECUTTER) {
            return asType(new MenuTypeData<>(StonecutterView.class, () -> new CraftAccessLocationInventoryViewBuilder<>(handle, StonecutterScreenHandler::new)));
        }

        return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftStandardInventoryViewBuilder<>(handle)));
    }

    @SuppressWarnings("unchecked")
    private static <V extends InventoryView, B extends InventoryViewBuilder<V>> MenuTypeData<V, B> asType(final MenuTypeData<?, ?> data) {
        return (MenuTypeData<V, B>) data;
    }
}

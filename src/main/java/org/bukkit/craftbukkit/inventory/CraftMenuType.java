package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.javazilla.bukkitfabric.interfaces.IMixinScreenHandler;

import io.papermc.paper.world.flag.PaperFeatureDependent;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.util.CraftMenus;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.cardboardpowered.adventure.CardboardAdventure;

public class CraftMenuType<V extends InventoryView> implements MenuType.Typed<V>, Handleable<ScreenHandlerType<?>>, PaperFeatureDependent {

    private final NamespacedKey key;
    private final ScreenHandlerType<?> handle;
    private final Supplier<CraftMenus.MenuTypeData<V>> typeData;

    public CraftMenuType(NamespacedKey key, ScreenHandlerType<?> handle) {
        this.key = key;
        this.handle = handle;
        this.typeData = Suppliers.memoize(() -> CraftMenus.getMenuTypeData(this));
    }

    @Override
    public ScreenHandlerType<?> getHandle() {
        return this.handle;
    }

    public V create(HumanEntity player, String title) {
        return this.create(player, (Component)LegacyComponentSerializer.legacySection().deserialize(title));
    }

    public V create(HumanEntity player, Component title) {
        Preconditions.checkArgument((player != null ? 1 : 0) != 0, "The given player must not be null");
        Preconditions.checkArgument((title != null ? 1 : 0) != 0, "The given title must not be null");
        Preconditions.checkArgument(player instanceof CraftHumanEntity, "The given player must be a CraftHumanEntity");
        CraftHumanEntity craftHuman = (CraftHumanEntity)player;
        Preconditions.checkArgument(craftHuman.getHandle() instanceof ServerPlayerEntity, "The given player must be an EntityPlayer");
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity)craftHuman.getHandle();
        ScreenHandler container = this.typeData.get().menuBuilder().build(serverPlayer, this.handle);
        IMixinScreenHandler iscreen = (IMixinScreenHandler) container;
        
        iscreen.setTitle(CardboardAdventure.asVanilla(title));
        iscreen.setCheckReachable(false);
        return (V) iscreen.getBukkitView();
    }

    public MenuType.Typed<InventoryView> typed() {
        return this.typed(InventoryView.class);
    }

    public <V extends InventoryView> MenuType.Typed<V> typed(Class<V> clazz) {
        if (clazz.isAssignableFrom(this.typeData.get().viewClass())) {
            return (Typed<V>) this;
        }
        throw new IllegalArgumentException("Cannot type InventoryView " + this.key.toString() + " to InventoryView type " + clazz.getSimpleName());
    }

    public Class<? extends InventoryView> getInventoryViewClass() {
        return this.typeData.get().viewClass();
    }

    public NamespacedKey getKey() {
        return this.key;
    }

    public static ScreenHandlerType<?> bukkitToMinecraft(MenuType bukkit) {
        return (ScreenHandlerType)CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static MenuType minecraftToBukkit(ScreenHandlerType<?> minecraft) {
        return (MenuType)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.SCREEN_HANDLER, Registry.MENU);
    }

    public static MenuType minecraftHolderToBukkit(RegistryEntry<ScreenHandlerType<?>> minecraft) {
        return CraftMenuType.minecraftToBukkit(minecraft.value());
    }

}

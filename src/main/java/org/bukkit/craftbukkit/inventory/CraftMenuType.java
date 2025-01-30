package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.util.CraftMenus;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.builder.InventoryViewBuilder;
import org.jetbrains.annotations.NotNull;

public class CraftMenuType<V extends InventoryView, B extends InventoryViewBuilder<V>> implements MenuType.Typed<V, B>, Handleable<net.minecraft.screen.ScreenHandlerType<?>>, io.papermc.paper.world.flag.PaperFeatureDependent { // Paper - make FeatureDependant

    private final NamespacedKey key;
    private final net.minecraft.screen.ScreenHandlerType<?> handle;
    private final Supplier<CraftMenus.MenuTypeData<V, B>> typeData;

    public CraftMenuType(NamespacedKey key, net.minecraft.screen.ScreenHandlerType<?> handle) {
        this.key = key;
        this.handle = handle;
        this.typeData = Suppliers.memoize(() -> CraftMenus.getMenuTypeData(this));
    }

    @Override
    public net.minecraft.screen.ScreenHandlerType<?> getHandle() {
        return this.handle;
    }

    @Override
    public V create(final HumanEntity player, final String title) {
    // Paper start - adventure
        return builder().title(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().deserialize(title)).build(player);
    }
    @Override
    public V create(final HumanEntity player, final net.kyori.adventure.text.Component title) {
    // Paper end - adventure
        return builder().title(title).build(player);
    }

    @Override
    public B builder() {
        return typeData.get().viewBuilder().get();
    }

    @Override
    public Typed<InventoryView, InventoryViewBuilder<InventoryView>> typed() {
        return this.typed(InventoryView.class);
    }

    @Override
    public <V extends InventoryView, B extends InventoryViewBuilder<V>> Typed<V, B> typed(Class<V> clazz) {
        if (clazz.isAssignableFrom(this.typeData.get().viewClass())) {
            return (Typed<V, B>) this;
        }

        throw new IllegalArgumentException("Cannot type InventoryView " + this.key.toString() + " to InventoryView type " + clazz.getSimpleName());
    }

    @Override
    public Class<? extends InventoryView> getInventoryViewClass() {
        return this.typeData.get().viewClass();
    }

    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    public static net.minecraft.screen.ScreenHandlerType<?> bukkitToMinecraft(MenuType bukkit) {
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static MenuType minecraftToBukkit(net.minecraft.screen.ScreenHandlerType<?> minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.SCREEN_HANDLER);
    }

    public static MenuType minecraftHolderToBukkit(RegistryEntry<net.minecraft.screen.ScreenHandlerType<?>> minecraft) {
        return CraftMenuType.minecraftToBukkit(minecraft.value());
    }
}

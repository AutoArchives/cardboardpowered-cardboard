package io.papermc.paper.datacomponent.item;

import org.bukkit.craftbukkit.util.Handleable;

public record PaperEnchantable(
    net.minecraft.component.type.EnchantableComponent impl
) implements Enchantable, Handleable<net.minecraft.component.type.EnchantableComponent> {

    @Override
    public net.minecraft.component.type.EnchantableComponent getHandle() {
        return this.impl;
    }

    @Override
    public int value() {
        return this.impl.value();
    }
}

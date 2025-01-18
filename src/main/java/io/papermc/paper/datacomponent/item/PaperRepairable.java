package io.papermc.paper.datacomponent.item;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.PaperRegistrySets;
import io.papermc.paper.registry.set.RegistryKeySet;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.inventory.ItemType;

public record PaperRepairable(
    net.minecraft.component.type.RepairableComponent impl
) implements Repairable, Handleable<net.minecraft.component.type.RepairableComponent> {

    @Override
    public net.minecraft.component.type.RepairableComponent getHandle() {
        return this.impl;
    }

    @Override
    public RegistryKeySet<ItemType> types() {
        return PaperRegistrySets.convertToApi(RegistryKey.ITEM, this.impl.items());
    }
}

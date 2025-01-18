package io.papermc.paper.datacomponent.item;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.inventory.ItemStack;

public record PaperUseRemainder(
    net.minecraft.component.type.UseRemainderComponent impl
) implements UseRemainder, Handleable<net.minecraft.component.type.UseRemainderComponent> {

    @Override
    public net.minecraft.component.type.UseRemainderComponent getHandle() {
        return this.impl;
    }

    @Override
    public ItemStack transformInto() {
        return CraftItemStack.asBukkitCopy(this.impl.convertInto());
    }
}

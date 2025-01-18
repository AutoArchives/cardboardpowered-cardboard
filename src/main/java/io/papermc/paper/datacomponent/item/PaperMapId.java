package io.papermc.paper.datacomponent.item;

import org.bukkit.craftbukkit.util.Handleable;

public record PaperMapId(
    net.minecraft.component.type.MapIdComponent impl
) implements MapId, Handleable<net.minecraft.component.type.MapIdComponent> {

    @Override
    public net.minecraft.component.type.MapIdComponent getHandle() {
        return this.impl;
    }

    @Override
    public int id() {
        return this.impl.id();
    }

}

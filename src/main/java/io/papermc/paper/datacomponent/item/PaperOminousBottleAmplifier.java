package io.papermc.paper.datacomponent.item;

import org.bukkit.craftbukkit.util.Handleable;

public record PaperOminousBottleAmplifier(
    net.minecraft.component.type.OminousBottleAmplifierComponent impl
) implements OminousBottleAmplifier, Handleable<net.minecraft.component.type.OminousBottleAmplifierComponent> {

    @Override
    public net.minecraft.component.type.OminousBottleAmplifierComponent getHandle() {
        return this.impl;
    }

    @Override
    public int amplifier() {
        return this.impl.value();
    }
}

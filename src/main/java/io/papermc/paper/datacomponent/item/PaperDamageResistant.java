package io.papermc.paper.datacomponent.item;

import io.papermc.paper.registry.PaperRegistries;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.damage.DamageType;

public record PaperDamageResistant(
    net.minecraft.component.type.DamageResistantComponent impl
) implements DamageResistant, Handleable<net.minecraft.component.type.DamageResistantComponent> {

    @Override
    public net.minecraft.component.type.DamageResistantComponent getHandle() {
        return this.impl;
    }

    @Override
    public TagKey<DamageType> types() {
        return PaperRegistries.fromNms(this.impl.types());
    }
}

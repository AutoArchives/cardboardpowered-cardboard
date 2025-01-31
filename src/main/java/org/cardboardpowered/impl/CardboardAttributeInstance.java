package org.cardboardpowered.impl;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.attribute.CraftAttributeInstance;

@Deprecated(forRemoval = true)
public class CardboardAttributeInstance extends CraftAttributeInstance {

    public CardboardAttributeInstance(EntityAttributeInstance handle, Attribute attribute) {
        super(handle, attribute);
    }

}

package org.cardboardpowered.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.util.Handleable;

@Deprecated(forRemoval = true)
public class CardboardAttributable extends CraftAttribute implements Attribute, Handleable<net.minecraft.entity.attribute.EntityAttribute> {

    public CardboardAttributable(NamespacedKey key, net.minecraft.entity.attribute.EntityAttribute attributeBase) {
    	super(key, attributeBase);
    }

}

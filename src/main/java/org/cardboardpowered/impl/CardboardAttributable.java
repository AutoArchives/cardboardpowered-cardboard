package org.cardboardpowered.impl;

import java.util.Optional;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.jetbrains.annotations.NotNull;

import me.isaiah.common.ICommonMod;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntry.Reference;

public class CardboardAttributable implements Attributable {

	private final AttributeContainer handle;

    public CardboardAttributable(AttributeContainer handle) {
        this.handle = handle;
    }

    @Override
    public AttributeInstance getAttribute(Attribute attribute) {
    	// EntityAttributeInstance nms = handle.getCustomInstance(toMinecraft(attribute));
        // return (nms == null) ? null : new CardboardAttributeInstance(nms, attribute);
    	
    	Optional<Reference<EntityAttribute>> attr = toMinecraft_1(attribute);
    	
    	if (!attr.isPresent()) {
    		return null;
    	}
    	
    	EntityAttributeInstance nms = handle.getCustomInstance(attr.get());
    	return (nms == null) ? null : new CardboardAttributeInstance(nms, attribute);
    }

    public static EntityAttribute toMinecraft(Attribute attribute) {
        return net.minecraft.registry.Registries.ATTRIBUTE.get(CraftNamespacedKey.toMinecraft(attribute.getKey()));
    }
    
    public static Optional<Reference<EntityAttribute>> toMinecraft_1(Attribute attribute) {
        return net.minecraft.registry.Registries.ATTRIBUTE.getEntry(CraftNamespacedKey.toMinecraft(attribute.getKey()));
    }

    public static Attribute fromMinecraft(String nms) {
        return Registry.ATTRIBUTE.get(CraftNamespacedKey.fromString(nms));
    }

    @Override
    public void registerAttribute(Attribute attribute) {
        // TODO Paper API
    }

	public static void bukkitToMinecraftHolder() {
		// TODO Auto-generated method stub
		
	}
	
    public static EntityAttribute bukkitToMinecraft(Attribute bukkit) {
        // Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
    	
    	return Registries.ATTRIBUTE.getOrEmpty(CraftNamespacedKey.toMinecraft(bukkit.getKey())).orElseThrow();
    	
        // return (EntityAttribute)CraftRegistry.getMinecraftRegistry(RegistryKeys.ATTRIBUTE).getOrEmpty(CraftNamespacedKey.toMinecraft(bukkit.getKey())).orElseThrow();
    }
	
    public static RegistryEntry<EntityAttribute> bukkitToMinecraftHolder(Attribute bukkit) {
        // Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        net.minecraft.registry.Registry registry = Registries.ATTRIBUTE;// CraftRegistry.getMinecraftRegistry(RegistryKeys.ATTRIBUTE);
        RegistryEntry<EntityAttribute> registryEntry = registry.getEntry(bukkitToMinecraft(bukkit));
        if (registryEntry instanceof RegistryEntry.Reference) {
            RegistryEntry.Reference holder = (RegistryEntry.Reference)registryEntry;
            return holder;
        }
        throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own sound effect with out properly registering it.");
    }

    public static Attribute stringToBukkit(String bukkit) {
        // Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        NamespacedKey key = NamespacedKey.fromString((String)bukkit);
        return key != null ? (Attribute)Registry.ATTRIBUTE.get(key) : null;
    }

    public static Attribute minecraftHolderToBukkit(RegistryEntry<EntityAttribute> minecraft) {
        return minecraftToBukkit(minecraft.value());
    }
    
    public static Attribute minecraftToBukkit(EntityAttribute minecraft) {
        // Preconditions.checkArgument((minecraft != null ? 1 : 0) != 0);
    	
    	net.minecraft.registry.Registry<EntityAttribute> registry = ICommonMod.getIServer().getMinecraft().getRegistryManager().get(RegistryKeys.ATTRIBUTE);
    	
        // net.minecraft.registry.Registry registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.ATTRIBUTE);
        Attribute bukkit = (Attribute)Registry.ATTRIBUTE.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft).orElseThrow().getValue()));
       //  Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        return bukkit;
    }

}
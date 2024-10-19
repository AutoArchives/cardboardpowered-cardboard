package org.cardboardpowered.impl;

import net.kyori.adventure.key.Key;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.javazilla.bukkitfabric.Utils;

import me.isaiah.common.cmixin.IMixinEntityAttributeModifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CardboardAttributeInstance implements AttributeInstance {

    private final EntityAttributeInstance handle;
    private final Attribute attribute;

    public CardboardAttributeInstance(EntityAttributeInstance handle, Attribute attribute) {
        this.handle = handle;
        this.attribute = attribute;
    }

    @Override
    public Attribute getAttribute() {
        return attribute;
    }

    @Override
    public double getBaseValue() {
        return handle.getBaseValue();
    }

    @Override
    public void setBaseValue(double d) {
        handle.setBaseValue(d);
    }

    @Override
    public Collection<AttributeModifier> getModifiers() {
        List<AttributeModifier> result = new ArrayList<AttributeModifier>();
        for (EntityAttributeModifier nms : handle.getModifiers()) result.add(convert(nms));
        return result;
    }

    @Override
    public void addModifier(AttributeModifier modifier) {
        handle.addPersistentModifier(convert(modifier));
    }

    @Override
    public double getValue() {
        return handle.getValue();
    }

    @Override
    public double getDefaultValue() {
       // return handle.getAttribute().getDefaultValue();
    	return handle.getAttribute().value().getDefaultValue();
    }

    public static EntityAttributeModifier convert(AttributeModifier bukkit) {
        
    	// TODO
    	
    	Identifier pseudo_id = Identifier.of(bukkit.getName());
    	
        return new EntityAttributeModifier(pseudo_id, bukkit.getAmount(), EntityAttributeModifier.Operation.values()[bukkit.getOperation().ordinal()]);

    	
    	
    	// return new EntityAttributeModifier(bukkit.getUniqueId(), bukkit.getName(), bukkit.getAmount(), EntityAttributeModifier.Operation.values()[bukkit.getOperation().ordinal()]);
    }

    public static AttributeModifier convert(EntityAttributeModifier nms) {
    	// IMixinEntityAttributeModifier ic = (IMixinEntityAttributeModifier) (Object) nms;
    	
    	return new AttributeModifier(CraftNamespacedKey.fromMinecraft(nms.id()).toString(), nms.value(), AttributeModifier.Operation.values()[nms.operation().ordinal()]);
    	
        // TODO
    	// return new AttributeModifier(ic.IC$get_uuid(), nms.name, ic.IC$get_value(), AttributeModifier.Operation.values()[ic.IC$get_operation().ordinal()]);
    }

	// @Override
    public void addTransientModifier(AttributeModifier modifier) {
        this.handle.addTemporaryModifier(convert(modifier));
    }
	
    public static AttributeModifier convert(EntityAttributeModifier nms, EquipmentSlot slot) {
    	IMixinEntityAttributeModifier ic = (IMixinEntityAttributeModifier) (Object) nms;
    	
    	// TODO
    	
        return new AttributeModifier(ic.IC$get_uuid(), nms.id().toString(), ic.IC$get_value(), AttributeModifier.Operation.values()[ic.IC$get_operation().ordinal()], slot);
    }

	@Override
	public @Nullable AttributeModifier getModifier(@NotNull UUID uuid) {
		//EntityAttributeModifier modifier = this.handle.getModifier(AttributeMappings.uuidToKey(uuid));
		// return modifier == null ? null : CardboardAttributeInstance.convert(modifier);
		return this.getModifier(AttributeMappings.uuidToKey(uuid));
	}
	
    public AttributeModifier getModifier(Key key) {
        EntityAttributeModifier modifier = this.handle.getModifier(CardboardAdventure.asVanilla(key));
        return modifier == null ? null : convert(modifier);
    }

    @Override
    public void removeModifier(AttributeModifier modifier) {
        // todo
    	removeModifier(modifier.getUniqueId());
    }
	
	@Override
	public void removeModifier(@NotNull UUID uuid) {
		this.removeModifier(AttributeMappings.uuidToKey(uuid));
	}
	
	public void removeModifier(Key key) {
        this.handle.removeModifier(CardboardAdventure.asVanilla(key));
    }

    public static AttributeModifier convert(EntityAttributeModifier nms, AttributeModifierSlot slot) {
        
    	// TODO

    	return new AttributeModifier(CraftNamespacedKey.fromMinecraft(nms.id()).toString(), nms.value(), AttributeModifier.Operation.values()[nms.operation().ordinal()]);
    	
    	/*
    	return new AttributeModifier(
        		nms.id(),
        		null, // nms..name,
        		nms.value(),
        		AttributeModifier.Operation.values()[nms.operation().ordinal()],
        		Utils.getSlot(slot)
        );*/
    }
    
    /*
    public static AttributeModifier convert(EntityAttributeModifier nms) {
        return new AttributeModifier(
        		CraftNamespacedKey.fromMinecraft(nms.id()),
        		nms.value(), AttributeModifier.Operation.values()[nms.operation().ordinal()], EquipmentSlotGroup.ANY);
    }
    */

}

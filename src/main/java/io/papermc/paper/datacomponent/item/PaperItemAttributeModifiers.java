package io.papermc.paper.datacomponent.item;

import com.google.common.base.Preconditions;

import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import io.papermc.paper.datacomponent.item.attribute.PaperAttributeModifierDisplay;
import io.papermc.paper.util.MCUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.component.type.AttributeModifiersComponent;

import java.util.List;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.attribute.CraftAttributeInstance;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.Unmodifiable;

public record PaperItemAttributeModifiers(
    net.minecraft.component.type.AttributeModifiersComponent impl
) implements ItemAttributeModifiers, Handleable<net.minecraft.component.type.AttributeModifiersComponent> {

    private static List<Entry> convert(final net.minecraft.component.type.AttributeModifiersComponent nmsModifiers) {
        return MCUtil.transformUnmodifiable(nmsModifiers.modifiers(), nms -> new PaperEntry(
        		CraftAttribute.minecraftHolderToBukkit(nms.attribute()),
        		CraftAttributeInstance.convert(nms.modifier(), nms.slot()),
        		PaperAttributeModifierDisplay.fromNms(nms.display())
        ));
    }

    @Override
    public net.minecraft.component.type.AttributeModifiersComponent getHandle() {
        return this.impl;
    }

    @Override
    public @Unmodifiable List<Entry> modifiers() {
        return convert(this.impl);
    }

    public record PaperEntry(Attribute attribute, AttributeModifier modifier, AttributeModifierDisplay display) implements ItemAttributeModifiers.Entry {
    }

    static final class BuilderImpl implements ItemAttributeModifiers.Builder {

        private final List<net.minecraft.component.type.AttributeModifiersComponent.Entry> entries = new ObjectArrayList<>();

        @Override
        public io.papermc.paper.datacomponent.item.ItemAttributeModifiers.Builder addModifier(final Attribute attribute, final AttributeModifier modifier) {
            return this.addModifier(attribute, modifier, modifier.getSlotGroup());
        }

        @Override
        public ItemAttributeModifiers.Builder addModifier(final Attribute attribute, final AttributeModifier modifier, final EquipmentSlotGroup equipmentSlotGroup) {
            Preconditions.checkArgument(
                this.entries.stream().noneMatch(e ->
                    e.modifier().id().equals(CraftNamespacedKey.toMinecraft(modifier.getKey())) && e.attribute().matchesId(CraftNamespacedKey.toMinecraft(attribute.getKey()))
                ),
                "Cannot add 2 modifiers with identical keys on the same attribute (modifier %s for attribute %s)",
                modifier.getKey(), attribute.getKey()
            );

            this.entries.add(new net.minecraft.component.type.AttributeModifiersComponent.Entry(
            		CraftAttribute.bukkitToMinecraftHolder(attribute),
                CraftAttributeInstance.convert(modifier),
                CraftEquipmentSlot.getNMSGroup(equipmentSlotGroup)
            ));
            return this;
        }
        
        public ItemAttributeModifiers.Builder addModifier(Attribute attribute, AttributeModifier modifier, EquipmentSlotGroup equipmentSlotGroup, AttributeModifierDisplay display) {
            Preconditions.checkArgument((boolean)this.entries.stream().noneMatch(e2 -> e2.modifier().id().equals(CraftNamespacedKey.toMinecraft(modifier.getKey())) && e2.attribute().matchesId(CraftNamespacedKey.toMinecraft(attribute.getKey()))), (String)"Cannot add 2 modifiers with identical keys on the same attribute (modifier %s for attribute %s)", (Object)modifier.getKey(), (Object)attribute.getKey());
            this.entries.add(new AttributeModifiersComponent.Entry(CraftAttribute.bukkitToMinecraftHolder(attribute), CraftAttributeInstance.convert(modifier), CraftEquipmentSlot.getNMSGroup(equipmentSlotGroup), PaperAttributeModifierDisplay.toNms(display)));
            return this;
        }

        @Override
        public ItemAttributeModifiers build() {
            if (this.entries.isEmpty()) {
                return new PaperItemAttributeModifiers(net.minecraft.component.type.AttributeModifiersComponent.DEFAULT
                		/*.withShowInTooltip(this.showInTooltip)*/);
            }

            return new PaperItemAttributeModifiers(new net.minecraft.component.type.AttributeModifiersComponent(
                new ObjectArrayList<>(this.entries) // , this.showInTooltip
            ));
        }
    }
}

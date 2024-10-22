package org.bukkit.craftbukkit;

import java.util.Locale;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Hand;
import org.bukkit.inventory.EquipmentSlotGroup;

public class CraftEquipmentSlot {

    private static final EquipmentSlot[] slots = new EquipmentSlot[org.bukkit.inventory.EquipmentSlot.values().length];
    private static final org.bukkit.inventory.EquipmentSlot[] enums = new org.bukkit.inventory.EquipmentSlot[EquipmentSlot.values().length];

    private static void set(org.bukkit.inventory.EquipmentSlot type, EquipmentSlot value) {
        CraftEquipmentSlot.slots[type.ordinal()] = value;
        CraftEquipmentSlot.enums[value.ordinal()] = type;
    }

    public static org.bukkit.inventory.EquipmentSlot getSlot(EquipmentSlot nms) {
        return enums[nms.ordinal()];
    }

    public static EquipmentSlotGroup getSlot(AttributeModifierSlot nms) {
        return EquipmentSlotGroup.getByName((String)nms.asString());
    }

    public static EquipmentSlot getNMS(org.bukkit.inventory.EquipmentSlot slot) {
        return slots[slot.ordinal()];
    }

    public static AttributeModifierSlot getNMSGroup(EquipmentSlotGroup slot) {
        return AttributeModifierSlot.valueOf(slot.toString().toUpperCase(Locale.ROOT));
    }

    public static org.bukkit.inventory.EquipmentSlot getHand(Hand enumhand) {
        return enumhand == Hand.MAIN_HAND ? org.bukkit.inventory.EquipmentSlot.HAND : org.bukkit.inventory.EquipmentSlot.OFF_HAND;
    }

    public static Hand getHand(org.bukkit.inventory.EquipmentSlot hand) {
        if (hand == org.bukkit.inventory.EquipmentSlot.HAND) {
            return Hand.MAIN_HAND;
        }
        if (hand == org.bukkit.inventory.EquipmentSlot.OFF_HAND) {
            return Hand.OFF_HAND;
        }
        throw new IllegalArgumentException("EquipmentSlot." + String.valueOf(hand) + " is not a hand");
    }

    static {
        CraftEquipmentSlot.set(org.bukkit.inventory.EquipmentSlot.HAND, EquipmentSlot.MAINHAND);
        CraftEquipmentSlot.set(org.bukkit.inventory.EquipmentSlot.OFF_HAND, EquipmentSlot.OFFHAND);
        CraftEquipmentSlot.set(org.bukkit.inventory.EquipmentSlot.FEET, EquipmentSlot.FEET);
        CraftEquipmentSlot.set(org.bukkit.inventory.EquipmentSlot.LEGS, EquipmentSlot.LEGS);
        CraftEquipmentSlot.set(org.bukkit.inventory.EquipmentSlot.CHEST, EquipmentSlot.CHEST);
        CraftEquipmentSlot.set(org.bukkit.inventory.EquipmentSlot.HEAD, EquipmentSlot.HEAD);
        CraftEquipmentSlot.set(org.bukkit.inventory.EquipmentSlot.BODY, EquipmentSlot.BODY);
    }

}

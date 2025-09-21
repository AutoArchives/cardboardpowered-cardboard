package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.CowVariant;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.cardboardpowered.impl.entity.CraftAnimals;
import io.papermc.paper.registry.HolderableBase;

public class CraftCow extends CraftAnimals implements Cow {

    public CraftCow(CraftServer server, CowEntity entity) {
        super(server, entity);
    }

    @Override
    public CowEntity getHandle() {
        return (CowEntity) nms;
    }

    @Override
    public String toString() {
        return "CraftCow";
    }

    @Override
    public EntityType getType() {
        return EntityType.COW;
    }
    
    public Cow.Variant getVariant() {
        return CraftVariant.minecraftHolderToBukkit(this.getHandle().getVariant());
    }

    public void setVariant(Cow.Variant variant) {
        this.getHandle().setVariant(CraftVariant.bukkitToMinecraftHolder(variant));
    }

    public static class CraftVariant extends HolderableBase<CowVariant> implements Cow.Variant {

        public static Cow.Variant minecraftToBukkit(CowVariant minecraft) {
            return (Cow.Variant)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.COW_VARIANT);
        }

        public static Cow.Variant minecraftHolderToBukkit(RegistryEntry<CowVariant> minecraft) {
            return (Cow.Variant)CraftRegistry.minecraftHolderToBukkit(minecraft, RegistryKeys.COW_VARIANT);
        }

        public static CowVariant bukkitToMinecraft(Cow.Variant bukkit) {
            return (CowVariant)CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public static RegistryEntry<CowVariant> bukkitToMinecraftHolder(Cow.Variant bukkit) {
            return CraftRegistry.bukkitToMinecraftHolder(bukkit);
        }

        public CraftVariant(RegistryEntry<CowVariant> holder) {
            super(holder);
        }

    }

}
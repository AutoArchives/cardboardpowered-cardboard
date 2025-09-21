package org.cardboardpowered.impl.entity;

import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;

import io.papermc.paper.registry.HolderableBase;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PigVariant;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public class CardboardPig extends CraftAnimals implements Pig {

    public CardboardPig(CraftServer server, PigEntity entity) {
        super(server, entity);
    }

    @Override
    public boolean hasSaddle() {
        return this.getHandle().hasSaddleEquipped();
    }

    @Override
    public void setSaddle(boolean saddled) {
        // TODO implement
    }

    @Override
    public int getBoostTicks() {
        // TODO implement
        return 0;
    }

    @Override
    public void setBoostTicks(int ticks) {
        // TODO implement
    }

    @Override
    public int getCurrentBoostTicks() {
        // TODO implement
        return 0;
    }

    @Override
    public void setCurrentBoostTicks(int ticks) {
        // TODO implement
    }

    @Override
    public Material getSteerMaterial() {
        return Material.CARROT_ON_A_STICK;
    }

    @Override
    public PigEntity getHandle() {
        return (PigEntity)this.nms;
    }

    @Override
    public String toString() {
        return "Pig";
    }

    @Override
    public EntityType getType() {
        return EntityType.PIG;
    }
    
    public Pig.Variant getVariant() {
        return CraftVariant.minecraftHolderToBukkit(this.getHandle().getVariant());
    }

    public void setVariant(Pig.Variant variant) {
        // TODO: set vis.
        // this.getHandle().setVariant(CraftVariant.bukkitToMinecraftHolder(variant));
    }

    public static class CraftVariant
    extends HolderableBase<PigVariant>
    implements Pig.Variant {
        public static Pig.Variant minecraftToBukkit(PigVariant minecraft) {
            return (Pig.Variant)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.PIG_VARIANT);
        }

        public static Pig.Variant minecraftHolderToBukkit(RegistryEntry<PigVariant> minecraft) {
            return (Pig.Variant)CraftRegistry.minecraftHolderToBukkit(minecraft, RegistryKeys.PIG_VARIANT);
        }

        public static PigVariant bukkitToMinecraft(Pig.Variant bukkit) {
            return (PigVariant)CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public static RegistryEntry<PigVariant> bukkitToMinecraftHolder(Pig.Variant bukkit) {
            return CraftRegistry.bukkitToMinecraftHolder(bukkit);
        }

        public CraftVariant(RegistryEntry<PigVariant> holder) {
            super(holder);
        }
    }

}
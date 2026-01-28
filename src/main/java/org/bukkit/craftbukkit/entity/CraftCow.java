package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import io.papermc.paper.registry.HolderableBase;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.animal.cow.CowVariant;

public class CraftCow extends CraftAnimals implements Cow {

    public CraftCow(CraftServer server, net.minecraft.world.entity.animal.cow.Cow entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.cow.Cow getHandle() {
        return (net.minecraft.world.entity.animal.cow.Cow) entity;
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
            return (Cow.Variant)CraftRegistry.minecraftToBukkit(minecraft, Registries.COW_VARIANT);
        }

        public static Cow.Variant minecraftHolderToBukkit(Holder<CowVariant> minecraft) {
            return (Cow.Variant)CraftRegistry.minecraftHolderToBukkit(minecraft, Registries.COW_VARIANT);
        }

        public static CowVariant bukkitToMinecraft(Cow.Variant bukkit) {
            return (CowVariant)CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public static Holder<CowVariant> bukkitToMinecraftHolder(Cow.Variant bukkit) {
            return CraftRegistry.bukkitToMinecraftHolder(bukkit);
        }

        public CraftVariant(Holder<CowVariant> holder) {
            super(holder);
        }

    }

}
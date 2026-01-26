package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.Locale;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.entity.Frog;
import org.cardboardpowered.impl.entity.CraftAnimals;

public class CraftFrog extends CraftAnimals implements Frog {

    public CraftFrog(CraftServer server, net.minecraft.world.entity.animal.frog.Frog entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.frog.Frog getHandle() {
        return (net.minecraft.world.entity.animal.frog.Frog) this.entity;//this.entity;
    }

    @Override
    public String toString() {
        return "CraftFrog";
    }

    public org.bukkit.entity.Entity getTongueTarget() {
        return this.getHandle().getTongueTarget().map(Entity::getBukkitEntity).orElse(null);
    }

    public void setTongueTarget(org.bukkit.entity.Entity target) {
        if (target == null) {
            this.getHandle().eraseTongueTarget();
        } else {
            this.getHandle().setTongueTarget(((CraftEntity)target).getHandle());
        }
    }

    public Frog.Variant getVariant() {
        return CraftVariant.minecraftHolderToBukkit((Holder<FrogVariant>)this.getHandle().getVariant());
    }

    public void setVariant(Frog.Variant variant) {
        Preconditions.checkArgument((variant != null ? 1 : 0) != 0, (Object)"variant");
        this.getHandle().setVariant(CraftVariant.bukkitToMinecraftHolder(variant));
    }

    public static class CraftVariant implements Frog.Variant, Handleable<FrogVariant> {
        private static int count = 0;
        private final NamespacedKey key;
        private final FrogVariant frogVariant;
        private final String name;
        private final int ordinal;

        public static Frog.Variant minecraftToBukkit(FrogVariant minecraft) {
            return (Frog.Variant)CraftRegistry.minecraftToBukkit(minecraft, Registries.FROG_VARIANT);
        }

        public static Frog.Variant minecraftHolderToBukkit(Holder<FrogVariant> minecraft) {
            return CraftVariant.minecraftToBukkit(minecraft.value());
        }

        public static FrogVariant bukkitToMinecraft(Frog.Variant bukkit) {
            return (FrogVariant)CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public static Holder<FrogVariant> bukkitToMinecraftHolder(Frog.Variant bukkit) {
            return CraftRegistry.bukkitToMinecraftHolder(bukkit, Registries.FROG_VARIANT);
        }

        public CraftVariant(NamespacedKey key, FrogVariant frogVariant) {
            this.key = key;
            this.frogVariant = frogVariant;
            this.name = "minecraft".equals(key.getNamespace()) ? key.getKey().toUpperCase(Locale.ROOT) : key.toString();
            this.ordinal = count++;
        }

        @Override
        public FrogVariant getHandle() {
            return this.frogVariant;
        }

        public NamespacedKey getKey() {
            return this.key;
        }

        public int compareTo(Frog.Variant variant) {
            return this.ordinal - variant.ordinal();
        }

        public String name() {
            return this.name;
        }

        public int ordinal() {
            return this.ordinal;
        }

        public String toString() {
            return this.name();
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CraftVariant)) {
                return false;
            }
            return this.getKey().equals((Object)((Frog.Variant)other).getKey());
        }

        public int hashCode() {
            return this.getKey().hashCode();
        }
    }

}
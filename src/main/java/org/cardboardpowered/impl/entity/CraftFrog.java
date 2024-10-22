package org.cardboardpowered.impl.entity;

import com.google.common.base.Preconditions;
import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.entity.Frog;

public class CraftFrog
extends AnimalsImpl
implements Frog {
    public CraftFrog(CraftServer server, FrogEntity entity) {
        super(server, entity);
    }

    @Override
    public FrogEntity getHandle() {
        return (FrogEntity) this.nms;//this.entity;
    }

    @Override
    public String toString() {
        return "CraftFrog";
    }

    public org.bukkit.entity.Entity getTongueTarget() {
        return this.getHandle().getFrogTarget().map(Entity::getBukkitEntity).orElse(null);
    }

    public void setTongueTarget(org.bukkit.entity.Entity target) {
        if (target == null) {
            this.getHandle().clearFrogTarget();
        } else {
            this.getHandle().setFrogTarget(((CraftEntity)target).getHandle());
        }
    }

    public Frog.Variant getVariant() {
        return CraftVariant.minecraftHolderToBukkit((RegistryEntry<FrogVariant>)this.getHandle().getVariant());
    }

    public void setVariant(Frog.Variant variant) {
        Preconditions.checkArgument((variant != null ? 1 : 0) != 0, (Object)"variant");
        this.getHandle().setVariant(CraftVariant.bukkitToMinecraftHolder(variant));
    }

    public static class CraftVariant
    implements Frog.Variant,
    Handleable<FrogVariant> {
        private static int count = 0;
        private final NamespacedKey key;
        private final FrogVariant frogVariant;
        private final String name;
        private final int ordinal;

        public static Frog.Variant minecraftToBukkit(FrogVariant minecraft) {
            return (Frog.Variant)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.FROG_VARIANT, Registry.FROG_VARIANT);
        }

        public static Frog.Variant minecraftHolderToBukkit(RegistryEntry<FrogVariant> minecraft) {
            return CraftVariant.minecraftToBukkit(minecraft.value());
        }

        public static FrogVariant bukkitToMinecraft(Frog.Variant bukkit) {
            return (FrogVariant)CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public static RegistryEntry<FrogVariant> bukkitToMinecraftHolder(Frog.Variant bukkit) {
            return CraftRegistry.bukkitToMinecraftHolder(bukkit, RegistryKeys.FROG_VARIANT);
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


package org.cardboardpowered.impl.entity;

import com.google.common.base.Preconditions;
import com.javazilla.bukkitfabric.BukkitFabricMod;

import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Locale;

import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;

public class CardboardCat extends TameableAnimalImpl implements Cat {

	public static class CraftType
    implements Cat.Type,
    Handleable<CatVariant> {
        private static int count = 0;
        private final NamespacedKey key;
        private final CatVariant catVariant;
        private final String name;
        private final int ordinal;

        public static Cat.Type minecraftToBukkit(CatVariant minecraft) {
            return (Cat.Type)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.CAT_VARIANT, Registry.CAT_VARIANT);
        }

        public static Cat.Type minecraftHolderToBukkit(RegistryEntry<CatVariant> minecraft) {
            return CraftType.minecraftToBukkit(minecraft.value());
        }

        public static CatVariant bukkitToMinecraft(Cat.Type bukkit) {
            return (CatVariant)CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public static RegistryEntry<CatVariant> bukkitToMinecraftHolder(Cat.Type bukkit) {
            return CraftRegistry.bukkitToMinecraftHolder(bukkit, RegistryKeys.CAT_VARIANT);
        }

        public CraftType(NamespacedKey key, CatVariant catVariant) {
            this.key = key;
            this.catVariant = catVariant;
            this.name = "minecraft".equals(key.getNamespace()) ? key.getKey().toUpperCase(Locale.ROOT) : key.toString();
            this.ordinal = count++;
        }

        @Override
        public CatVariant getHandle() {
            return this.catVariant;
        }

        public NamespacedKey getKey() {
            return this.key;
        }

        public int compareTo(Cat.Type variant) {
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
            if (!(other instanceof CraftType)) {
                return false;
            }
            return this.getKey().equals((Object)((CraftType)other).getKey());
        }

        public int hashCode() {
            return this.getKey().hashCode();
        }
    }

	public CardboardCat(CraftServer server, CatEntity entity) {
        super(server, entity);
    }

    @Override
    public CatEntity getHandle() {
        return (CatEntity) super.getHandle();
    }

    @Override
    public EntityType getType() {
        return EntityType.CAT;
    }

    @Override
    public String toString() {
        return "Cat";
    }

    @Override
    public Type getCatType() {
        return Type.ALL_BLACK;// TODO: 1.19: Type.values()[getHandle().getCatType()];
    }

    @Override
    public void setCatType(Type type) {
        if (null == type) {
            BukkitFabricMod.LOGGER.info("Error: Cannot have null Cat Type, defaulting to ALL_BLACK");
            type = Type.ALL_BLACK;
        }

       // getHandle().setCatType(type.ordinal());
    }

    @SuppressWarnings("deprecation")
    @Override
    public DyeColor getCollarColor() {
        return DyeColor.getByWoolData((byte) getHandle().getCollarColor().getId());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setCollarColor(DyeColor color) {
        getHandle().setCollarColor(net.minecraft.util.DyeColor.byId(color.getWoolData()));
    }

    @Override
    public boolean isHeadUp() {
        return !getHandle().isHeadDown();
    }

    @Override
    public boolean isLyingDown() {
        return getHandle().isSitting();
    }

    @Override
    public void setHeadUp(boolean bl) {
        getHandle().setHeadDown(!bl);
    }

    @Override
    public void setLyingDown(boolean bl) {
        getHandle().setSitting(bl);
    }

}
package org.cardboardpowered.impl.entity;

import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

public class WolfImpl extends TameableAnimalImpl implements Wolf {

    public WolfImpl(CraftServer server, WolfEntity wolf) {
        super(server, wolf);
    }

    @Override
    public boolean isAngry() {
        return getHandle().hasAngerTime();
    }

    @Override
    public void setAngry(boolean angry) {
        if (angry) {
            getHandle().chooseRandomAngerTime();
        } else getHandle().stopAnger();
    }

    @Override
    public WolfEntity getHandle() {
        return (WolfEntity) nms;
    }

    @Override
    public EntityType getType() {
        return EntityType.WOLF;
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
	public boolean isInterested() {
		return this.getHandle().isBegging();
	}

	@Override
	public void setInterested(boolean arg0) {
		this.getHandle().setBegging(arg0);
	}

	@Override
	public float getTailAngle() {
		 return this.getHandle().getTailAngle();
	}

	@Override
	public boolean isWet() {
		 return this.getHandle().isFurWet();
	}

	// 1.20.6 API
	
    @Override
    public Variant getVariant() {
        return CraftVariant.minecraftHolderToBukkit(this.getHandle().getVariant());
    }

    @Override
    public void setVariant(Variant variant) {
        Preconditions.checkArgument(variant != null, "variant");

        this.getHandle().setVariant(CraftVariant.bukkitToMinecraftHolder(variant));
    }
	
	public static class CraftVariant implements Variant, Handleable<WolfVariant> {

        public static Variant minecraftToBukkit(WolfVariant minecraft) {
            return CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.WOLF_VARIANT, org.bukkit.Registry.WOLF_VARIANT);
        }

        public static Variant minecraftHolderToBukkit(RegistryEntry<WolfVariant> minecraft) {
            return CraftVariant.minecraftToBukkit(minecraft.value());
        }

        public static WolfVariant bukkitToMinecraft(Variant bukkit) {
            return CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public static RegistryEntry<WolfVariant> bukkitToMinecraftHolder(Variant bukkit) {
            Preconditions.checkArgument(bukkit != null);
            
            net.minecraft.registry.Registry<WolfVariant> registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.WOLF_VARIANT);

            if (registry.getEntry(CraftVariant.bukkitToMinecraft(bukkit)) instanceof RegistryEntry.Reference<WolfVariant> holder) {
                return holder;
            }

            throw new IllegalArgumentException("No Reference holder found for " + bukkit
                    + ", this can happen if a plugin creates its own wolf variant with out properly registering it.");
        }

        private final NamespacedKey key;
        private final WolfVariant variant;

        public CraftVariant(NamespacedKey key, WolfVariant variant) {
            this.key = key;
            this.variant = variant;
        }

        @Override
        public WolfVariant getHandle() {
            return this.variant;
        }

        @Override
        public NamespacedKey getKey() {
            return this.key;
        }

        @Override
        public String toString() {
            return this.key.toString();
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }

            if (!(other instanceof CraftVariant otherVariant)) {
                return false;
            }

            return this.getKey().equals(otherVariant.getKey());
        }

        @Override
        public int hashCode() {
            return this.getKey().hashCode();
        }
    }

}
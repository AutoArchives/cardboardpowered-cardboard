package org.bukkit.craftbukkit.entity;

import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

import io.papermc.paper.registry.HolderableBase;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariant;

public class CraftWolf extends CraftTameableAnimal implements Wolf {

    public CraftWolf(CraftServer server, net.minecraft.world.entity.animal.wolf.Wolf wolf) {
        super(server, wolf);
    }

    @Override
    public boolean isAngry() {
        return getHandle().isAngry();
    }

    @Override
    public void setAngry(boolean angry) {
        if (angry) {
            getHandle().startPersistentAngerTimer();
        } else getHandle().stopBeingAngry();
    }

    @Override
    public net.minecraft.world.entity.animal.wolf.Wolf getHandle() {
        return (net.minecraft.world.entity.animal.wolf.Wolf) entity;
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
        getHandle().setCollarColor(net.minecraft.world.item.DyeColor.byId(color.getWoolData()));
    }

	@Override
	public boolean isInterested() {
		return this.getHandle().isInterested();
	}

	@Override
	public void setInterested(boolean arg0) {
		this.getHandle().setIsInterested(arg0);
	}

	@Override
	public float getTailAngle() {
		 return this.getHandle().getTailAngle();
	}

	@Override
	public boolean isWet() {
		 return this.getHandle().isWet;
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
            return CraftRegistry.minecraftToBukkit(minecraft, Registries.WOLF_VARIANT);
        }

        public static Variant minecraftHolderToBukkit(Holder<WolfVariant> minecraft) {
            return CraftVariant.minecraftToBukkit(minecraft.value());
        }

        public static WolfVariant bukkitToMinecraft(Variant bukkit) {
            return CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public static Holder<WolfVariant> bukkitToMinecraftHolder(Variant bukkit) {
            Preconditions.checkArgument(bukkit != null);
            
            net.minecraft.core.Registry<WolfVariant> registry = CraftRegistry.getMinecraftRegistry(Registries.WOLF_VARIANT);

            if (registry.wrapAsHolder(CraftVariant.bukkitToMinecraft(bukkit)) instanceof Holder.Reference<WolfVariant> holder) {
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

	@Override
	public @NotNull SoundVariant getSoundVariant() {
		return null; // TODO
		// return CraftSoundVariant.minecraftHolderToBukkit(this.getHandle().getSoundVariant());
	}

	@Override
	public void setSoundVariant(@NotNull SoundVariant soundVariant) {
		// TODO Auto-generated method stub
		// this.getHandle().setSoundVariant(CraftSoundVariant.bukkitToMinecraftHolder(soundVariant));
	}
	
	public static class CraftSoundVariant extends HolderableBase<WolfSoundVariant> implements Wolf.SoundVariant {
        public static Wolf.SoundVariant minecraftToBukkit(WolfSoundVariant minecraft) {
            return (Wolf.SoundVariant)CraftRegistry.minecraftToBukkit(minecraft, Registries.WOLF_SOUND_VARIANT);
        }

        public static Wolf.SoundVariant minecraftHolderToBukkit(Holder<WolfSoundVariant> minecraft) {
            return (Wolf.SoundVariant)CraftRegistry.minecraftHolderToBukkit(minecraft, Registries.WOLF_SOUND_VARIANT);
        }

        public static WolfSoundVariant bukkitToMinecraft(Wolf.SoundVariant bukkit) {
            return (WolfSoundVariant)CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public static Holder<WolfSoundVariant> bukkitToMinecraftHolder(Wolf.SoundVariant bukkit) {
            return CraftRegistry.bukkitToMinecraftHolder(bukkit);
        }

        public CraftSoundVariant(Holder<WolfSoundVariant> holder) {
            super(holder);
        }
    }

}
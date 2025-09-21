package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.cardboardpowered.impl.entity.CraftAnimals;

import io.papermc.paper.registry.HolderableBase;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.ChickenVariant;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public class CraftChicken extends CraftAnimals implements Chicken {

    public CraftChicken(CraftServer server, ChickenEntity entity) {
        super(server, entity);
    }

    @Override
    public ChickenEntity getHandle() {
        return (ChickenEntity) nms;
    }

    @Override
    public String toString() {
        return "Chicken";
    }

    @Override
    public EntityType getType() {
        return EntityType.CHICKEN;
    }

	@Override
	public int getEggLayTime() {
		return this.getHandle().eggLayTime;
	}

	@Override
	public boolean isChickenJockey() {
		return this.getHandle().hasJockey();
	}

	@Override
	public void setEggLayTime(int arg0) {
        this.getHandle().eggLayTime = arg0;
	}

	@Override
	public void setIsChickenJockey(boolean arg0) {
		this.getHandle().setHasJockey(arg0);
	}
	
	public Chicken.Variant getVariant() {
        return CraftVariant.minecraftHolderToBukkit(this.getHandle().getVariant());
    }

    public void setVariant(Chicken.Variant variant) {
        this.getHandle().setVariant(CraftVariant.bukkitToMinecraftHolder(variant));
    }
    
    public static class CraftVariant extends HolderableBase<ChickenVariant> implements Chicken.Variant {
        public static Chicken.Variant minecraftToBukkit(ChickenVariant minecraft) {
            return (Chicken.Variant)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.CHICKEN_VARIANT);
        }

        public static Chicken.Variant minecraftHolderToBukkit(RegistryEntry<ChickenVariant> minecraft) {
            return (Chicken.Variant)CraftRegistry.minecraftHolderToBukkit(minecraft, RegistryKeys.CHICKEN_VARIANT);
        }

        public static ChickenVariant bukkitToMinecraft(Chicken.Variant bukkit) {
            return (ChickenVariant)CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public static RegistryEntry<ChickenVariant> bukkitToMinecraftHolder(Chicken.Variant bukkit) {
            return CraftRegistry.bukkitToMinecraftHolder(bukkit);
        }

        public CraftVariant(RegistryEntry<ChickenVariant> holder) {
            super(holder);
        }
    }

}